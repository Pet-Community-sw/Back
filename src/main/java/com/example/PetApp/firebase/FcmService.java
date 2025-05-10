package com.example.PetApp.firebase;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    public String sendNotification(String token, String title, String body) {

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        log.info("Fcm Notification 생성");
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();
        log.info("Fcm Message 생성");
        try {
            log.info("fcm에 보냄 요청");
            return FirebaseMessaging.getInstance().send(message);

        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT)) {
                // 토큰이 유효하지 않은 경우, 오류 코드를 반환
                return e.getMessagingErrorCode().toString();
            } else if (e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                // 재발급된 이전 토큰인 경우, 오류 코드를 반환
                return e.getMessagingErrorCode().toString();
            }
            else { // 그 외, 오류는 런타임 예외로 처리
                throw new RuntimeException(e);
            }
        }
    }
}