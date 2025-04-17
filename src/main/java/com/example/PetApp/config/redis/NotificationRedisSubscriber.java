package com.example.PetApp.config.redis;

import com.example.PetApp.service.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRedisSubscriber {
    private final SseEmitterManager sseEmitterManager;

    public void onMessage(String channel, String message) {
        log.info("notification channel:{}, message:{}", channel, message);

        Long profileId = Long.valueOf(channel.split(":")[1]);
        sseEmitterManager.sendNotification(profileId, message);
    }
}
