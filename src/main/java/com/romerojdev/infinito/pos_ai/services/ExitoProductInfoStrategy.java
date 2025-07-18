package com.romerojdev.infinito.pos_ai.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.romerojdev.infinito.pos_ai.dto.ProductInfoDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ExitoProductInfoStrategy implements ProductInfoStrategy {
    private static final Logger logger = Logger.getLogger(ExitoProductInfoStrategy.class.getName());
    private static final String BASE_URL = "https://www.exito.com";
    private static final String SEARCH_URL = BASE_URL + "/s?q=%s";
    private static final String PRODUCT_API_URL = BASE_URL + "/io/api/catalog_system/pub/products/search?ft=%s";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object getProductInfo(String barcode) {
        try {
            logger.info("Searching for product with barcode: " + barcode);

            // First try the direct product API with full-text search
            String url = String.format(PRODUCT_API_URL, barcode);
            ProductInfoDTO result = fetchProductInfoFromApi(url, barcode);

            // If API fails, try scraping the search results page
            if (result == null) {
                logger.info("API search failed, trying HTML scraping approach");
                result = scrapeProductInfoFromSearchPage(barcode);
            }

            // If HTML scraping also fails, try the GraphQL API
            if (result == null) {
                logger.info("HTML scraping failed, trying GraphQL API approach");
                result = fetchProductInfoFromGraphQL(barcode);
            }

            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting product info from Exito for barcode " + barcode, e);
            return fallbackProductInfo(barcode);
        }
    }

    /**
     * Fetch product info from Exito's API
     */
    private ProductInfoDTO fetchProductInfoFromApi(String url, String barcode) {
        try {
            logger.info("Requesting product info from API: " + url);

            // Set up headers to mimic a browser request
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
            headers.set("Accept", "application/json");
            headers.set("Origin", BASE_URL);
            headers.set("Referer", String.format(SEARCH_URL, barcode));

            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Make the request to the API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            logger.info("API response status: " + response.getStatusCode());
            logger.info("API response body: " + (responseBody != null ? responseBody : "null"));

            // Check if response body is empty array or null
            if (responseBody == null || responseBody.trim().isEmpty() || responseBody.equals("[]")) {
                logger.info("Empty or null API response for barcode: " + barcode + ". Falling back to HTML scraping.");
                return null;
            }

            // Check if the response is valid JSON
            if (!isValidJson(responseBody)) {
                logger.warning("Response is not valid JSON: " + responseBody.substring(0, Math.min(100, responseBody.length())) + "...");
                return null;
            }

            // Parse the JSON response
            JsonNode products = mapper.readTree(responseBody);

            // If we got an empty array, return null
            if (products.isArray() && products.size() == 0) {
                logger.info("No products found for barcode: " + barcode);
                return null;
            }

            // Get the first product (most relevant)
            JsonNode product = products.isArray() ? products.get(0) : products;

            // Create ProductInfoDTO.Product object
            ProductInfoDTO.Product productDto = new ProductInfoDTO.Product();
            productDto.code = barcode;

            // Extract product name
            if (product.has("productName")) {
                productDto.product_name = product.get("productName").asText();
            }

            // Extract brand name
            if (product.has("brand")) {
                productDto.brands = product.get("brand").asText();
            }

            // Extract product description
            if (product.has("description")) {
                productDto.generic_name = product.get("description").asText();
            }

            // Extract image URL
            if (product.has("items") && product.get("items").isArray() && product.get("items").size() > 0) {
                JsonNode item = product.get("items").get(0);
                if (item.has("images") && item.get("images").isArray() && item.get("images").size() > 0) {
                    productDto.image_url = item.get("images").get(0).get("imageUrl").asText();
                }
            }

            // Extract price
            if (product.has("items") && product.get("items").isArray() && product.get("items").size() > 0) {
                JsonNode item = product.get("items").get(0);
                if (item.has("sellers") && item.get("sellers").isArray() && item.get("sellers").size() > 0) {
                    JsonNode seller = item.get("sellers").get(0);
                    if (seller.has("commertialOffer") && seller.get("commertialOffer").has("Price")) {
                        double price = seller.get("commertialOffer").get("Price").asDouble();
                        // Format price as string with currency
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
                        productDto.product_quantity = format.format(price);
                    }
                }
            }

            // Set store information
            productDto.stores = "Exito";

            return new ProductInfoDTO(barcode, productDto);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching from API URL " + url, e);
            return null;
        }
    }

    /**
     * Scrape product info from Exito's search results page
     */
    private ProductInfoDTO scrapeProductInfoFromSearchPage(String barcode) {
        try {
            String searchUrl = String.format(SEARCH_URL, barcode);
            logger.info("Scraping search results from: " + searchUrl);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Make the request
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String html = response.getBody();
            logger.info("HTML scraping response status: " + response.getStatusCode());

            if (html == null || html.isEmpty()) {
                logger.info("Empty HTML response for barcode: " + barcode);
                return null;
            }

            // Parse HTML with Jsoup
            Document doc = Jsoup.parse(html);

            // Create product object
            ProductInfoDTO.Product product = new ProductInfoDTO.Product();
            product.code = barcode;
            product.stores = "Exito";

            // Find the first product in search results - try multiple selector patterns
            Elements productElements = doc.select("div[data-testid='product-summary']");
            if (productElements.isEmpty()) {
                // Try alternative selectors if the primary one doesn't work
                productElements = doc.select(".vtex-search-result-3-x-galleryItem");
                if (productElements.isEmpty()) {
                    productElements = doc.select(".vtex-product-summary-2-x-container");
                    if (productElements.isEmpty()) {
                        // Try a more generic approach if specific selectors fail
                        productElements = doc.select("div[class*='product-summary']");
                    }
                }
            }

            if (productElements.isEmpty()) {
                logger.info("No products found in search results for barcode: " + barcode);
                // Save HTML content for debugging (first 500 chars)
                logger.info("HTML preview: " + html.substring(0, Math.min(500, html.length())));
                return null;
            }

            Element productElement = productElements.first();
            logger.info("Found product element: " + productElement.outerHtml().substring(0, Math.min(200, productElement.outerHtml().length())));

            // Extract product name
            Elements nameElements = productElement.select("h3");
            if (nameElements.isEmpty()) {
                nameElements = productElement.select("[class*='productName']");
                if (nameElements.isEmpty()) {
                    nameElements = productElement.select("span[class*='name']");
                }
            }

            if (!nameElements.isEmpty()) {
                product.product_name = nameElements.first().text().trim();
                logger.info("Found product name: " + product.product_name);
            }

            // Extract price
            Elements priceElements = productElement.select("span[data-testid='price']");
            if (priceElements.isEmpty()) {
                priceElements = productElement.select("[class*='price']");
                if (priceElements.isEmpty()) {
                    priceElements = productElement.select("span[class*='currencyContainer']");
                }
            }

            if (!priceElements.isEmpty()) {
                product.product_quantity = priceElements.first().text().trim();
                logger.info("Found product price: " + product.product_quantity);
            }

            // Extract image URL
            Elements imageElements = productElement.select("img");
            if (!imageElements.isEmpty()) {
                product.image_url = imageElements.first().attr("src");
                if (product.image_url == null || product.image_url.isEmpty()) {
                    product.image_url = imageElements.first().attr("data-src");
                }
                logger.info("Found product image: " + product.image_url);
            }

            // If we found a product name, return the result
            if (product.product_name != null && !product.product_name.isEmpty()) {
                return new ProductInfoDTO(barcode, product);
            }

            return null;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error scraping search results for barcode " + barcode, e);
            return null;
        }
    }

    /**
     * Fetch product info using Exito's GraphQL API
     * This method uses the GraphQL API endpoint discovered from browser inspection
     */
    private ProductInfoDTO fetchProductInfoFromGraphQL(String barcode) {
        try {
            logger.info("Requesting product info from GraphQL API for barcode: " + barcode);

            // Set up the GraphQL endpoint URL
            String graphqlUrl = BASE_URL + "/api/graphql?operationName=QuerySearch";

            // Create the GraphQL query payload
            String graphqlPayload = String.format(
                "{\"operationName\":\"QuerySearch\",\"variables\":{\"first\":5,\"after\":\"0\",\"sort\":\"score_desc\",\"term\":\"%s\",\"selectedFacets\":[{\"key\":\"channel\",\"value\":\"{\\\"salesChannel\\\":\\\"1\\\",\\\"regionId\\\":\\\"\\\"}\"}," +
                "{\"key\":\"locale\",\"value\":\"es-CO\"}]}}",
                barcode
            );

            // Set up headers to mimic a browser request
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
            headers.set("Accept", "*/*");
            headers.set("Accept-Language", "en-US,en;q=0.9,es-CO;q=0.8,es;q=0.7");
            headers.set("Content-Type", "application/json");
            headers.set("Origin", BASE_URL);
            headers.set("Referer", String.format(SEARCH_URL, barcode));

            // Add cookies if needed (we'll add a few key ones)
            headers.add("Cookie", "__rtbh.lid={\"eventType\":\"lid\",\"id\":\"f00LqlYcJ5kvmolbbvhL\",\"expiryDate\":\"2026-07-09T02:53:06.210Z\"}");

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
            logger.info("GraphQL API response preview: " + (responseBody != null ? responseBody.substring(0, Math.min(200, responseBody.length())) + "..." : "null"));

            // Check if response body is empty or null
            if (responseBody == null || responseBody.trim().isEmpty()) {
                logger.info("Empty or null GraphQL API response for barcode: " + barcode);
                return null;
            }

            // Check if the response is valid JSON
            if (!isValidJson(responseBody)) {
                logger.warning("Response is not valid JSON: " + responseBody.substring(0, Math.min(100, responseBody.length())) + "...");
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

            // Create ProductInfoDTO.Product object
            ProductInfoDTO.Product productDto = new ProductInfoDTO.Product();
            productDto.code = barcode;

            // Extract product name
            if (product.has("name")) {
                productDto.product_name = product.get("name").asText();
            }

            // Extract brand name
            if (product.has("brand") && product.get("brand").has("name")) {
                productDto.brands = product.get("brand").get("name").asText();
            }

            // Extract product category from breadcrumbs if available
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

            // Extract image URL if available
            // The image URL might be in a different part of the GraphQL response
            // For now, we'll use the product slug to construct a typical product URL
            if (product.has("slug")) {
                productDto.image_url = BASE_URL + "/" + product.get("slug").asText() + "/p";
            }

            // Extract priceValidUntil from offers[0].priceValidUntil if available
            if (product.has("offers") && product.get("offers").isArray() && product.get("offers").size() > 0) {
                JsonNode offer = product.get("offers").get(0);
                if (offer.has("priceValidUntil")) {
                    String priceValidUntilStr = offer.get("priceValidUntil").asText();
                    if (priceValidUntilStr != null && !priceValidUntilStr.isEmpty()) {
                        try {
                            java.text.SimpleDateFormat isoDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            java.util.Date priceValidUntil = isoDateFormat.parse(priceValidUntilStr);
                            productDto.priceValidUntil = priceValidUntil;
                        } catch (Exception e) {
                            // Ignore parse errors
                        }
                    }
                }
            }

            // Extract price and priceWithoutDiscount from offers if available
            if (product.has("offers")) {
                JsonNode offersNode = product.get("offers");
                if (offersNode.has("lowPrice")) {
                    double price = offersNode.get("lowPrice").asDouble();
                    productDto.price = price;
                }
                if (offersNode.has("offers") && offersNode.get("offers").isArray() && offersNode.get("offers").size() > 0) {
                    JsonNode offerNode = offersNode.get("offers").get(0);
                    if (offerNode.has("listPrice")) {
                        double priceWithoutDiscount = offerNode.get("listPrice").asDouble();
                        productDto.priceWithoutDiscount = priceWithoutDiscount;
                    }
                }
            }
            // Calculate percentDiscount if both price and priceWithoutDiscount exist and priceWithoutDiscount > 0
            if (productDto.price != null && productDto.priceWithoutDiscount != null && productDto.priceWithoutDiscount > 0) {
                double percentDiscount = 100.0 * (productDto.priceWithoutDiscount - productDto.price) / productDto.priceWithoutDiscount;
                productDto.percentDiscount = percentDiscount;
            }

            // Set store information
            productDto.stores = "Exito";

            return new ProductInfoDTO(barcode, productDto);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching from GraphQL API for barcode " + barcode, e);
            return null;
        }
    }

    /**
     * Check if a string is valid JSON
     */
    private boolean isValidJson(String json) {
        try {
            mapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Create a fallback product info when API returns invalid data
     */
    private ProductInfoDTO fallbackProductInfo(String barcode) {
        ProductInfoDTO.Product product = new ProductInfoDTO.Product();
        product.code = barcode;
        product.product_name = "Product " + barcode;
        product.stores = "Exito (Partial Information)";

        return new ProductInfoDTO(barcode, product);
    }
}
