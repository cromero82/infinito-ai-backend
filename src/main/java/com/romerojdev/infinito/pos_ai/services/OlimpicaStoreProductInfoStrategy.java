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
import java.util.Date;
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
            if (script == null) return null;
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
        } catch (IOException e) {
            return null;
        }
    }
}
