package com.romerojdev.infinito.pos_ai.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romerojdev.infinito.pos_ai.dto.ProductInfoDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class OlimpicaStoreProductInfoStrategy implements ProductInfoStrategy {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    @Override
    public Object getProductInfo(String barcode) {
        return getProductInfo(barcode, (Object[]) null);
    }

    // Not an override, just a helper
    public Object getProductInfo(String barcode, Object... extraParams) {
        String cookie = extraParams != null && extraParams.length > 0 && extraParams[0] instanceof String ? (String) extraParams[0] : null;
        String url = "https://www.olimpica.com/" + barcode + "?_q=" + barcode + "&map=ft";
        try {
            org.jsoup.Connection connection = Jsoup.connect(url)
                    .header("Cookie", cookie != null ? cookie : "")
                    .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(10000)
                    .ignoreContentType(true);
            Document doc = connection.get();
            // Find the script tag with type application/ld+json that contains ItemList and Product
            Element script = doc.select("script[type=application/ld+json]").stream()
                .filter(e -> e.html().contains("ItemList") && e.html().contains("Product"))
                .findFirst().orElse(null);

            var productCathegories = extractFromStateTemplate(doc, mapper);
            if (script == null) {
                // Fallback: extract from <template data-type="json" data-varname="__STATE__">
                return productCathegories;
            }

            ProductInfoDTO dto = extractFromScriptTag(script, barcode, doc);
            // --- If productCathegories has values, set categories_hierarchy and price fields ---
            if (productCathegories != null && productCathegories.getProduct() != null && productCathegories.getProduct().categories_hierarchy != null) {
                dto.getProduct().categories_hierarchy = productCathegories.getProduct().categories_hierarchy;
                // Also set priceWithoutDiscount and percentDiscount if available from fallback
                if (productCathegories.getProduct().priceWithoutDiscount != null) {
                    dto.getProduct().priceWithoutDiscount = productCathegories.getProduct().priceWithoutDiscount;
                }
                if (productCathegories.getProduct().percentDiscount != null) {
                    dto.getProduct().percentDiscount = productCathegories.getProduct().percentDiscount;
                }
                dto.getProduct().categories = productCathegories.getProduct().categories;

                // Set department, category, subcategory from categories_hierarchy if available
                String[] cats = productCathegories.getProduct().categories_hierarchy;
                if (cats != null) {
                    if (cats.length > 0) dto.getProduct().department = cats[0];
                    if (cats.length > 1) dto.getProduct().category = cats[1];
                    if (cats.length > 2) dto.getProduct().subcategory = cats[2];
                }
            }
            dto.getProduct().unit = productCathegories.getProduct().unit;
            dto.getProduct().stores = productCathegories.getProduct().stores;
            dto.getProduct()._keywords = productCathegories.getProduct()._keywords;

            return dto;
        } catch (IOException e) {
            return null;
        }
    }

    // New internal function for fallback extraction
    private ProductInfoDTO extractFromStateTemplate(Document doc, ObjectMapper mapper) throws IOException {
        Element template = doc.selectFirst("template[data-type=json][data-varname=__STATE__]");
        if (template == null) return null;
        Element script = template.selectFirst("script");
        if (script == null) return null;
        String json = script.html();
        JsonNode state = mapper.readTree(json);

        // Find the main product key (starts with Product:)
        String productKey = state.fieldNames().next();
        for (String key : iterable(state.fieldNames())) {
            if (key.startsWith("Product:")) {
                productKey = key;
                break;
            }
        }
        JsonNode productNode = state.path(productKey);
        if (productNode.isMissingNode()) return null;

        ProductInfoDTO dto = new ProductInfoDTO();
        ProductInfoDTO.Product product = new ProductInfoDTO.Product();
        product.product_name = productNode.path("productName").asText(null);
        product.generic_name = productNode.path("description").asText(null);
        product.brands = productNode.path("brand").asText(null);
        // Images
        String itemKey = productKey + ".items({\"filter\":\"ALL\"}).0";
        JsonNode itemNode = state.path(itemKey);
        if (itemNode.has("images")) {
            JsonNode images = itemNode.path("images");
            if (images.isArray() && images.size() > 0) {
                String imageId = images.get(0).path("id").asText();
                JsonNode imageNode = state.path(imageId);
                if (imageNode != null && imageNode.has("imageUrl")) {
                    product.image_url = imageNode.path("imageUrl").asText(null);
                }
            }
        }
        // Stores (sellerName)
        String sellerKey = itemKey + ".sellers.0";
        JsonNode sellerNode = state.path(sellerKey);
        if (sellerNode != null && sellerNode.has("sellerName")) {
            product.stores = sellerNode.path("sellerName").asText(null);
        }
        // Unit (unit_multiplier)
        String unitKey = productKey + ".specificationGroups.0.specifications.1";
        JsonNode unitNode = state.path(unitKey);
        if (unitNode != null && unitNode.has("values")) {
            JsonNode values = unitNode.path("values").path("json");
            if (values.isArray() && values.size() > 0) {
                product.unit = values.get(0).asText(null);
            }
        }
        // Prices
        String priceKey = "$" + productKey + ".priceRange.sellingPrice";
        String listPriceKey = "$" + productKey + ".priceRange.listPrice";
        JsonNode priceNode = state.path(priceKey);
        JsonNode listPriceNode = state.path(listPriceKey);
        if (!priceNode.isMissingNode()) {
            product.price = priceNode.path("lowPrice").asDouble();
        }
        if (!listPriceNode.isMissingNode()) {
            product.priceWithoutDiscount = listPriceNode.path("lowPrice").asDouble();
        }
        if (product.price != null && product.priceWithoutDiscount != null && product.priceWithoutDiscount > 0) {
            product.percentDiscount = 100.0 * (product.priceWithoutDiscount - product.price) / product.priceWithoutDiscount;
        }
        // Categories
        if (productNode.has("categories")) {
            JsonNode cats = productNode.path("categories").path("json");
            if (cats.isArray()) {
                List<String> categories = new ArrayList<>();
                for (JsonNode cat : cats) {
                    String[] parts = cat.asText().split("/");
                    for (String part : parts) {
                        if (!part.isBlank() && !categories.contains(part)) {
                            categories.add(part);
                        }
                    }
                }
                product.categories_hierarchy = categories.stream().limit(3).toArray(String[]::new);
                // Add joined categories string as requested
                if (!categories.isEmpty()) {
                    String joined = String.join(" > ", categories.stream().limit(3).toArray(String[]::new));
                    product.categories = joined;
                }
                // Set _keywords as split of product_name
                if (product.product_name != null) {
                    String clean = product.product_name.replaceAll("[()\\-]", "");
                    product._keywords = clean.split("\\s+");
                }
            }
        }
        dto.setProduct( product);
        return dto;
    }

    // Extract product info from script tag logic moved here
    private ProductInfoDTO extractFromScriptTag(Element script, String barcode, Document doc) throws IOException {
        String json = script.html();
        JsonNode root = mapper.readTree(json);
        JsonNode itemNode = root.path("itemListElement").get(0).path("item");
        if (itemNode.isMissingNode()) return null;

        ProductInfoDTO.Product product = new ProductInfoDTO.Product();
        product.product_name = itemNode.path("name").asText(null);
        product.generic_name = itemNode.path("description").asText(null);
        product.brands = itemNode.path("brand").path("name").asText(null);
        product.image_url = itemNode.path("image").asText(null);
        product.stores = "Olimpica";
        product.code = barcode;
        // Price
        JsonNode offers = itemNode.path("offers");
        if (offers.has("lowPrice")) {
            product.price = offers.path("lowPrice").asDouble();
        }
        // priceValidUntil
        Date priceValidUntil = null;
        if (offers.has("offers") && offers.path("offers").isArray() && offers.path("offers").size() > 0) {
            String validUntil = offers.path("offers").get(0).path("priceValidUntil").asText(null);
            if (validUntil != null) {
                try {
                    priceValidUntil = isoDateFormat.parse(validUntil);
                    product.priceValidUntil = priceValidUntil;
                } catch (ParseException ignored) {}
            }
        }
        // Extract price and priceWithoutDiscount from HTML aria-labels if present
        Double htmlPrice = null, htmlPriceWithoutDiscount = null;
        // Use the specific class for the price savings element
        Element priceLabel = doc.select("span.vtex-product-price-1-x-savings.vtex-product-price-1-x-savings--summary[aria-label]").first();
        if (priceLabel != null) {
            String aria = priceLabel.attr("aria-label");
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(aria);
            java.util.List<Double> found = new java.util.ArrayList<>();
            while (m.find()) {
                try {
                    String val = m.group();
                    if (!val.isEmpty()) found.add(Double.parseDouble(val));
                } catch (Exception ignore) {}
            }
            if (found.size() >= 2) {
                double v1 = found.get(0), v2 = found.get(1);
                htmlPrice = Math.min(v1, v2);
                htmlPriceWithoutDiscount = Math.max(v1, v2);
            }
        }
        if (htmlPriceWithoutDiscount != null) product.priceWithoutDiscount = htmlPriceWithoutDiscount;
        if (product.price != null && product.priceWithoutDiscount != null && product.priceWithoutDiscount > 0) {
            product.percentDiscount = 100.0 * (product.priceWithoutDiscount - product.price) / product.priceWithoutDiscount;
        }
        ProductInfoDTO dto = new ProductInfoDTO(barcode, product);
        dto.setPriceValidUntil(priceValidUntil);
        return dto;
    }

    // Helper to iterate field names
    private static Iterable<String> iterable(final java.util.Iterator<String> iterator) {
        return () -> iterator;
    }
}
