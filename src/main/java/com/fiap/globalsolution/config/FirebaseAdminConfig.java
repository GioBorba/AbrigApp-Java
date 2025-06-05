package com.fiap.globalsolution.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseAdminConfig {

    @PostConstruct
    public void init() {
        try {
            String firebaseConfig = System.getenv("FIREBASE_CONFIG");
            if (firebaseConfig == null || firebaseConfig.isBlank()) {
                throw new IllegalStateException("Variável FIREBASE_CONFIG não foi definida.");
            }

            InputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase Admin inicializado com sucesso.");

        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar Firebase Admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
