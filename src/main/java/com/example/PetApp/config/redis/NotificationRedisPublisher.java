package com.example.PetApp.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRedisPublisher {

    private final RedisTemplate<String, Object> notificationRedisTemplate;

    public void publish(String topic, String message) {
        log.info("publishing notification topic: {}, message: {}", topic, message);
        notificationRedisTemplate.convertAndSend(topic, message);
    }
}
