package com.romerojdev.infinito.pos_ai.controllers.db;

import com.romerojdev.infinito.pos_ai.services.SpeechToTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private SpeechToTextService speechToTextService;

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
