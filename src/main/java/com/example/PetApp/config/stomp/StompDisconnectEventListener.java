package com.example.PetApp.config.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompDisconnectEventListener {

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener
    public void disconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String profileId = accessor.getUser() != null ? accessor.getUser().getName() : null;

        log.info(" disconnect 요청  sessionId: {}, profileId: {}", sessionId, profileId);
        if (profileId != null) {
            String chatRoomId = stringRedisTemplate.opsForValue().get("session:" + sessionId);
            if (chatRoomId != null) {
                stringRedisTemplate.opsForSet().remove("chatRoomId:" + chatRoomId + ":onlineMembers", profileId);
                stringRedisTemplate.delete("session:" + sessionId);

                log.info("Removed profileId {} from chatRoom {} online members", profileId, chatRoomId);

            }
        }

    }
}
