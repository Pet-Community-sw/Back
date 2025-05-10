//package com.example.PetApp.firebase;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.common.net.HttpHeaders;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//@Slf4j
//@Service
//@RequiredArgsConstructor//최적화 필요.
//public class FcmService1 {
//    private final String API_URL = "https://fcm.googleapis.com/v1/projects/reborn-bbf41/messages:send";
//    private final ObjectMapper objectMapper;
//
//    // 메시지를 구성하고 토큰을 받아서 FCM으로 메시지를 처리한다.
//    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
//        String message = makeMessage(targetToken, title, body);
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = RequestBody.create(message, // 만든 message body에 넣기
//                MediaType.get("application/json; charset=utf-8"));
//
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .post(requestBody)
//                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken()) // header에 포함
//                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
//                .build();
//        Response response = client.newCall(request).execute(); // 요청 보냄
//
//        System.out.println(response.body().string());
//    }
//
//    // FCM 전송 정보를 기반으로 메시지를 구성한다. (Object -> String)
//    private String makeMessage(String targetToken, String title, String body) throws com.fasterxml.jackson.core.JsonProcessingException  { // JsonParseException, JsonProcessingException
//        FcmMessage fcmMessage = FcmMessage.builder()
//                .message(FcmMessage.Message.builder()
//                        .token(targetToken)
//                        .notification(FcmMessage.Notification.builder()
//                                .title(title)
//                                .body(body)
//                                .image(null)
//                                .build()
//                        ).build()).validateOnly(false).build();
//        return objectMapper.writeValueAsString(fcmMessage);
//    }
//
//    // Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받는다.
//    private String getAccessToken() throws IOException {
//        final String firebaseConfigPath = "firebase/serviceAccountKey.json";//fcm에서 받은 파일경로를 지정.
//
//        try {
//            final GoogleCredentials googleCredentials = GoogleCredentials
//                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//            googleCredentials.refreshIfExpired();
//            log.info("access token: {}",googleCredentials.getAccessToken());
//            return googleCredentials.getAccessToken().getTokenValue();
//
//        } catch (IOException e) {
//            log.error("Google AccessToken 요청 중 IOException 발생", e);
//            throw new RuntimeException("Google AccessToken 요청 중 IOException 발생", e);
//        }
//    }
//}