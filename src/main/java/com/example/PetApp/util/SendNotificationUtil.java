package com.example.PetApp.util;

import com.example.PetApp.config.redis.NotificationRedisPublisher;
import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.notification.NotificationListDto;
//import com.example.PetApp.firebase.FcmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationUtil {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final NotificationRedisPublisher notificationRedisPublisher;
//    private final FcmService fcmService;

    public void sendNotification(Member member, String message) throws JsonProcessingException {
        String key = "notifications:" + member.getMemberId() + ":" + UUID.randomUUID();//알림 설정 최대 3일.
        NotificationListDto notificationListDto = new NotificationListDto(message, LocalDateTime.now());
        String json = objectMapper.writeValueAsString(notificationListDto);
        notificationRedisTemplate.opsForValue().set(key, json, Duration.ofDays(3));
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember("foreGroundMembers:", member.getMemberId().toString()))) {
            notificationRedisPublisher.publish("member:" + member.getMemberId(), message);
        }else {
            log.info("backGroundMember");
//            fcmService.sendNotification(member.getFcmToken().getFcmToken(), "명냥로드", message);
        }
    }
}
