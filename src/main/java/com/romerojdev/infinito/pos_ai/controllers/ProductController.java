package com.romerojdev.infinito.pos_ai.controllers;

import com.romerojdev.infinito.pos_ai.entities.Product;
import com.romerojdev.infinito.pos_ai.services.ProductService;
import com.romerojdev.infinito.pos_ai.services.SpeechToTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private SpeechToTextService speechToTextService;

    @GetMapping
    public Page<Product> getAll(
            @RequestParam(defaultValue = "") String barcodeOrName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAll(barcodeOrName, pageable);
    }

    @PostMapping("/speech-to-text")
    public ResponseEntity<String> speechToText(@RequestPart("file") MultipartFile file) {
        try {
            String result = speechToTextService.transcribe(file.getInputStream());
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing audio file: " + e.getMessage());
        }
    }
}
