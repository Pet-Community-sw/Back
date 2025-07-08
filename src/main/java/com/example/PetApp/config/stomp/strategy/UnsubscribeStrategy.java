package com.example.PetApp.config.stomp.strategy;

import com.example.PetApp.config.stomp.SubscribeInfo;
import com.example.PetApp.domain.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnsubscribeStrategy implements StompCommandStrategy {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        String subscriptionId = accessor.getSubscriptionId();
        log.info("[STOMP][UNSUBSCRIBE] 요청 수신 - subscriptionId: {}", subscriptionId);

        if (subscriptionId == null) {
            log.error("[STOMP][UNSUBSCRIBE] subscriptionId 누락");
            throw new IllegalArgumentException("subscriptionId 없음.");
        }

        String json = redisTemplate.opsForValue().get("subscriptionId:" + subscriptionId);
        if (json == null) {
            log.warn("[STOMP][UNSUBSCRIBE] Redis에 subscriptionId:{} 관련 정보 없음", subscriptionId);
            return; // 구독 정보가 없는 경우에도 오류로 처리하지 않음
        }

        try {
            SubscribeInfo info = objectMapper.readValue(json, SubscribeInfo.class);
            String key = (info.getChatRoomType() == ChatMessage.ChatRoomType.MANY
                    ? "chatRoomId:" : "memberChatRoomId:") + info.getChatRoomId() + ":onlineMembers";

            redisTemplate.opsForSet().remove(key, info.getUserId());
            redisTemplate.delete("subscriptionId:" + subscriptionId);

            log.info("[STOMP][UNSUBSCRIBE] 구독 해제 완료 - roomId: {}, userId: {}", info.getChatRoomId(), info.getUserId());
        } catch (JsonProcessingException e) {
            log.error("[STOMP][UNSUBSCRIBE] JSON 파싱 실패", e);
            throw new RuntimeException("subscribeInfo 변환 실패", e);
        }
    }
}
