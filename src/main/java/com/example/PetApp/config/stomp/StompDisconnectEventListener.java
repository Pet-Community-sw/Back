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

        log.info("DISCONNECT 요청 - sessionId: {}, profileId: {}", sessionId, profileId);

        if (profileId == null) {
            log.warn("DISCONNECT: 사용자 정보가 없는 세션입니다. sessionId: {}", sessionId);
            return;
        }

        String chatRoomId = stringRedisTemplate.opsForValue().get("session:" + sessionId);
        if (chatRoomId == null) {
            log.warn("DISCONNECT: sessionId {} 에 해당하는 chatRoomId 없음", sessionId);
            return;
        }

        stringRedisTemplate.opsForSet().remove("chatRoomId:" + chatRoomId + ":onlineMembers", profileId);
        stringRedisTemplate.delete("session:" + sessionId);
        stringRedisTemplate.opsForSet().remove("foreGroundMembers", profileId);
        //connect할 때는 webSocket이 필요할 때 접속. unConnect는 사용자가 앱 종료 할 때 발생 stomp 프레임이아님.


        log.info("DISCONNECT 처리 완료 - profileId {} removed from chatRoomId {}", profileId, chatRoomId);
    }
}
