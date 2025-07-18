package com.romerojdev.infinito.pos_ai.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romerojdev.infinito.pos_ai.config.ExitoApiConfig;
import com.romerojdev.infinito.pos_ai.dto.ProductInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ExitoGraphQLService {
    private static final Logger logger = Logger.getLogger(ExitoGraphQLService.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ExitoApiConfig exitoApiConfig;

    /**
     * Search for a product using Exito's GraphQL API
     * @param barcode The product barcode to search for
     * @return ProductInfoDTO object if found, null otherwise
     */
    public ProductInfoDTO searchProductByBarcode(String barcode) {
        try {
            logger.info("Searching product in Exito GraphQL API for barcode: " + barcode);

            // Set up the GraphQL endpoint URL
            String graphqlUrl = exitoApiConfig.getFullGraphqlUrl();

            // Create the GraphQL query payload
            String graphqlPayload = String.format(
                "{\"operationName\":\"QuerySearch\",\"variables\":{\"first\":5,\"after\":\"0\",\"sort\":\"score_desc\",\"term\":\"%s\",\"selectedFacets\":[{\"key\":\"channel\",\"value\":\"{\\\"salesChannel\\\":\\\"1\\\",\\\"regionId\\\":\\\"\\\"}\"}," +
                "{\"key\":\"locale\",\"value\":\"es-CO\"}]}}",
                barcode
            );

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", exitoApiConfig.getUserAgent());
            headers.set("Accept", "*/*");
            headers.set("Accept-Language", "en-US,en;q=0.9,es-CO;q=0.8,es;q=0.7");
            headers.set("Content-Type", "application/json");
            headers.set("Origin", exitoApiConfig.getBaseUrl());
            headers.set("Referer", exitoApiConfig.getBaseUrl() + "/s?q=" + barcode);

            // Add cookies
            headers.add("Cookie", exitoApiConfig.getFormattedCookies());

            HttpEntity<String> entity = new HttpEntity<>(graphqlPayload, headers);

            // Make the request to the GraphQL API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    graphqlUrl,
                    HttpMethod.POST,  // GraphQL uses POST
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            logger.info("GraphQL API response status: " + response.getStatusCode());
            logger.info("GraphQL API response preview: " + (responseBody != null ?
                responseBody.substring(0, Math.min(200, responseBody.length())) + "..." : "null"));

            if (responseBody == null || responseBody.trim().isEmpty()) {
                logger.info("Empty response from GraphQL API");
                return null;
            }

            // Parse the JSON response
            JsonNode rootNode = mapper.readTree(responseBody);

            // Check if there are search results
            if (!rootNode.has("data") || !rootNode.get("data").has("search") ||
                !rootNode.get("data").get("search").has("suggestions") ||
                !rootNode.get("data").get("search").get("suggestions").has("products") ||
                rootNode.get("data").get("search").get("suggestions").get("products").size() == 0) {
                logger.info("No products found in GraphQL search results for barcode: " + barcode);
                return null;
            }

            // Get the first product (most relevant)
            JsonNode product = rootNode.get("data").get("search").get("suggestions").get("products").get(0);
            logger.info("Found product in GraphQL API: " + product.toString());

            // Extract facets for department, category, subcategory
            JsonNode facets = rootNode.get("data").get("search").get("facets");
            String department = null, category = null, subcategory = null;
            if (facets != null && facets.isArray()) {
                for (JsonNode facet : facets) {
                    if (facet.has("key") && facet.has("values") && facet.get("values").isArray() && facet.get("values").size() > 0) {
                        String key = facet.get("key").asText();
                        String label = facet.get("values").get(0).get("label").asText();
                        if ("category-1".equals(key)) department = label;
                        else if ("category-2".equals(key)) category = label;
                        else if ("category-3".equals(key)) subcategory = label;
                    }
                }
            }

            ProductInfoDTO dto = buildProductInfoDTO(barcode, product);
            if (dto != null && dto.getProduct() != null) {
                dto.getProduct().department = department;
                dto.getProduct().category = category;
                dto.getProduct().subcategory = subcategory;
            }
            return dto;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calling Exito GraphQL API for barcode " + barcode, e);
            return null;
        }
    }

    /**
     * Build a ProductInfoDTO object from the GraphQL API response
     */
    private ProductInfoDTO buildProductInfoDTO(String barcode, JsonNode product) {
        try {
            ProductInfoDTO.Product productDto = new ProductInfoDTO.Product();
            productDto.code = barcode;
            productDto.stores = "Exito";

            // Extract product name
            if (product.has("name")) {
                productDto.product_name = product.get("name").asText();
            }

            // Extract brand name
            if (product.has("brand") && product.get("brand").has("name")) {
                productDto.brands = product.get("brand").get("name").asText();
            }

            // Extract product category from breadcrumbs
            if (product.has("breadcrumbList") &&
                product.get("breadcrumbList").has("itemListElement") &&
                product.get("breadcrumbList").get("itemListElement").size() > 0) {

                JsonNode breadcrumbs = product.get("breadcrumbList").get("itemListElement");
                StringBuilder categories = new StringBuilder();

                for (int i = 0; i < breadcrumbs.size(); i++) {
                    JsonNode breadcrumb = breadcrumbs.get(i);
                    if (breadcrumb.has("name") && !breadcrumb.get("name").asText().equals(productDto.product_name)) {
                        if (categories.length() > 0) {
                            categories.append(" > ");
                        }
                        categories.append(breadcrumb.get("name").asText());
                    }
                }

                productDto.generic_name = categories.toString();
            }

            // Extract GTIN (barcode) if available
            if (product.has("gtin")) {
                productDto.code = product.get("gtin").asText();
            }

            // Extract image URL from 'image' array
            if (product.has("image") && product.get("image").isArray() && product.get("image").size() > 0) {
                JsonNode imageNode = product.get("image").get(0);
                if (imageNode.has("url")) {
                    productDto.image_url = imageNode.get("url").asText();
                }
            } else if (product.has("items") && product.get("items").isArray() && product.get("items").size() > 0) {
                // Fallback: try to get image from items[0].images[0].imageUrl
                JsonNode item = product.get("items").get(0);
                if (item.has("images") && item.get("images").isArray() && item.get("images").size() > 0) {
                    JsonNode imageNode = item.get("images").get(0);
                    if (imageNode.has("imageUrl")) {
                        productDto.image_url = imageNode.get("imageUrl").asText();
                    }
                }
            }

            // Extract properties: Tipo de Producto, Unidad de Medida, Ump del Empaque 1 (Out)
            if (product.has("properties") && product.get("properties").isArray()) {
                for (JsonNode prop : product.get("properties")) {
                    if (prop.has("name") && prop.has("values") && prop.get("values").isArray() && prop.get("values").size() > 0) {
                        String name = prop.get("name").asText();
                        String value = prop.get("values").get(0).asText();
                        if ("Tipo de Producto".equalsIgnoreCase(name)) {
                            productDto.product_type = value;
                        } else if ("Unidad de Medida".equalsIgnoreCase(name)) {
                            productDto.unit = value;
                        } else if ("Ump del Empaque 1 (Out)".equalsIgnoreCase(name)) {
                            productDto.package_unit = value;
                        }
                    }
                }
            }

            // Extract price from facets (if available)
            // The facets are not in the product node, so this must be passed in or handled at a higher level if needed
            // For now, try to extract from sellers/commertialOffer as fallback
            if (product.has("sellers") && product.get("sellers").isArray() && product.get("sellers").size() > 0) {
                JsonNode seller = product.get("sellers").get(0);
                if (seller.has("commertialOffer") && seller.get("commertialOffer").has("Price")) {
                    double price = seller.get("commertialOffer").get("Price").asDouble();
                    productDto.product_quantity = String.valueOf(price);
                }
            }

            // Extract sellers from items
            productDto.sellers = null;
            if (product.has("items") && product.get("items").isArray()) {
                java.util.List<ProductInfoDTO.Product.Seller> sellersList = new java.util.ArrayList<>();
                for (JsonNode itemNode : product.get("items")) {
                    if (itemNode.has("sellers") && itemNode.get("sellers").isArray()) {
                        for (JsonNode sellerNode : itemNode.get("sellers")) {
                            ProductInfoDTO.Product.Seller seller = new ProductInfoDTO.Product.Seller();
                            seller.sellerId = sellerNode.has("sellerId") ? sellerNode.get("sellerId").asText() : null;
                            seller.sellerName = sellerNode.has("sellerName") ? sellerNode.get("sellerName").asText() : null;
                            seller.sellerDefault = sellerNode.has("sellerDefault") && sellerNode.get("sellerDefault").asBoolean();
                            if (sellerNode.has("commertialOffer")) {
                                JsonNode offerNode = sellerNode.get("commertialOffer");
                                ProductInfoDTO.Product.Seller.CommertialOffer offer = new ProductInfoDTO.Product.Seller.CommertialOffer();
                                offer.AvailableQuantity = offerNode.has("AvailableQuantity") ? offerNode.get("AvailableQuantity").asInt() : 0;
                                offer.Price = offerNode.has("Price") ? offerNode.get("Price").asDouble() : 0.0;
                                offer.PriceWithoutDiscount = offerNode.has("PriceWithoutDiscount") ? offerNode.get("PriceWithoutDiscount").asDouble() : 0.0;
                                offer.ListPrice = offerNode.has("ListPrice") ? offerNode.get("ListPrice").asDouble() : 0.0;
                                offer.Tax = offerNode.has("Tax") ? offerNode.get("Tax").asDouble() : 0.0;
                                seller.commertialOffer = offer;
                            }
                            sellersList.add(seller);
                        }
                    }
                }
                if (!sellersList.isEmpty()) {
                    productDto.sellers = sellersList;
                }
            }

            return new ProductInfoDTO(barcode, productDto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error building ProductInfoDTO from GraphQL response", e);
            return null;
        }
    }

    /**
     * Update the API configuration at runtime
     * This allows you to update cookies and other parameters without restarting the application
     */
    public void updateApiConfig(String rtbhLid, String gclLs, String spid) {
        if (rtbhLid != null && !rtbhLid.isEmpty()) {
            exitoApiConfig.setRtbhLid(rtbhLid);
        }

        if (gclLs != null && !gclLs.isEmpty()) {
            exitoApiConfig.setGclLs(gclLs);
        }

        if (spid != null && !spid.isEmpty()) {
            exitoApiConfig.setSpid(spid);
        }

        logger.info("Exito API configuration updated");
    }
}
