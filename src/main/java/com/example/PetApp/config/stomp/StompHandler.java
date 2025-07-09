package com.example.PetApp.config.stomp;

import com.example.PetApp.config.stomp.strategy.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final Map<StompCommand, StompCommandStrategy> strategyMap;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            log.warn("[STOMP] accessor is null. 메시지를 건너뜁니다.");
            return message;
        }

        StompCommand command = accessor.getCommand();
        log.info("[STOMP] preSend 진입 - command: {}, sessionId: {}, destination: {}",
                command, accessor.getSessionId(), accessor.getDestination());

        try {
            StompCommandStrategy strategy = strategyMap.get(command);
            if (strategy != null) {
                log.info("[STOMP] {} 명령 처리 시작", command);
                strategy.handle(accessor);
                log.info("[STOMP] {} 명령 처리 완료", command);
            } else {
                log.info("[STOMP] 처리할 전략이 등록되지 않은 명령: {}", command);
            }
        } catch (Exception e) {
            log.error("[STOMP] {} 명령 처리 중 오류 발생: {}", command, e.getMessage(), e);
            throw e;
        }

        return message;
    }
}
