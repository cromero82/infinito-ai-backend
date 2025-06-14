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
        // 1. Save uploaded audio to a temp file
        Path tempInput = Files.createTempFile("uploaded-audio", ".wav");
        Files.copy(audioStream, tempInput, StandardCopyOption.REPLACE_EXISTING);

        // 2. Convert to 16kHz, mono, 16-bit PCM WAV using ffmpeg and append 1s silence
        Path tempOutput = Files.createTempFile("converted-audio", ".wav");
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg", "-y",
            "-i", tempInput.toAbsolutePath().toString(),
            "-f", "lavfi", "-t", "1", "-i", "anullsrc=r=16000:cl=mono",
            "-filter_complex", "[0][1]concat=n=2:v=0:a=1",
            "-ar", "16000", // sample rate
            "-ac", "1",     // mono
            "-sample_fmt", "s16", // 16-bit PCM
            tempOutput.toAbsolutePath().toString()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[FFmpeg] " + line);
            }
        }
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg conversion failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg was interrupted", e);
        }

        // 3. Pass the converted file to Vosk
        try (InputStream convertedStream = Files.newInputStream(tempOutput);
             Recognizer recognizer = new Recognizer(model, 16000)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = convertedStream.read(buffer)) >= 0) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    System.out.println("[Vosk] Result: " + recognizer.getResult());
                } else {
                    System.out.println("[Vosk] Partial: " + recognizer.getPartialResult());
                }
            }
            String finalResult = recognizer.getResult();
            System.out.println("[Vosk] Final: " + finalResult);
            return finalResult;
        } finally {
            // Clean up temp files
            Files.deleteIfExists(tempInput);
            Files.deleteIfExists(tempOutput);
        }
    }
}
