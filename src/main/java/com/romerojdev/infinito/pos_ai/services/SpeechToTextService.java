package com.romerojdev.infinito.pos_ai.services;

import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class SpeechToTextService {
    private static final String MODEL_PATH = "model"; // Place your Vosk model in src/main/resources/model
    private Model model;

    public SpeechToTextService() throws IOException {
        LibVosk.vosk_set_log_level(0); // 0 = warnings, 1 = info, 2 = debug
        Path modelPath = Path.of("src/main/resources/", MODEL_PATH);
        if (!Files.exists(modelPath)) {
            throw new IOException("Vosk model not found at " + modelPath.toAbsolutePath());
        }
        this.model = new Model(modelPath.toString());
    }

    public String transcribe(InputStream audioStream) throws IOException {
        try (Recognizer recognizer = new Recognizer(model, 16000)) { // 16kHz sample rate
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioStream.read(buffer)) >= 0) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    // Optionally process partial results
                }
            }
            return recognizer.getResult();
        }
    }
}
