//package com.example.PetApp.firebase;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//import javax.annotation.PostConstruct;
//
//@Configuration
//public class FcmInitializer {
//
//    @Value("${firebase.service-account-file}")
//    private String serviceAccountFile;
//
//    @Value("${firebase.database-url}")
//    private String databaseUrl;
//
//    @PostConstruct
//    public void initialize() {
//        try {
//            InputStream serviceAccount =
//                    new ClassPathResource(serviceAccountFile).getInputStream();
//
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setDatabaseUrl(databaseUrl)
//                    .build();
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                System.out.println("파이어베이스 초기화");
//                FirebaseApp.initializeApp(options);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}