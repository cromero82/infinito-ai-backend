package com.romerojdev.infinito.pos_ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {
    // Use the official LibreTranslate endpoint (https)
    private static final String TRANSLATE_URL = "https://libretranslate.de/translate";
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    @Value("${deepl.api.key}")
    private String deeplApiKey;

    public String translateEnToEs(String text) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api-free.deepl.com/v2/translate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "DeepL-Auth-Key " + deeplApiKey);
        MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("text", text);
        params.add("source_lang", "EN");
        params.add("target_lang", "ES");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
            logger.info("DeepL HTTP status: {}", responseEntity.getStatusCode());
            logger.info("DeepL raw response: {}", responseEntity.getBody());
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map map = mapper.readValue(responseEntity.getBody(), Map.class);
                if (map != null && map.containsKey("translations")) {
                    Object translations = map.get("translations");
                    if (translations instanceof java.util.List && !((java.util.List) translations).isEmpty()) {
                        Object first = ((java.util.List) translations).get(0);
                        if (first instanceof Map && ((Map) first).containsKey("text")) {
                            return ((Map) first).get("text").toString();
                        }
                    }
                }
            }
            return text;
        } catch (Exception e) {
            logger.error("DeepL error: {}", e.getMessage(), e);
            return text;
        }
    }

    public boolean isEnglish(String text) {
        // Simple heuristic: checks if text contains only English letters, numbers, and common punctuation
        return text != null && text.matches("^[A-Za-z0-9 _.,!\"'/$]*$");
    }
}
