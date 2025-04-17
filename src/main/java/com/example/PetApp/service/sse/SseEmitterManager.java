package com.example.PetApp.service.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SseEmitterManager {

    private final static Long DEFAULT_TIMEOUT = 60 * 60 * 1000L;

    private final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();//스레드 중복 방지

    public SseEmitter subscribe(Long profileId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        sseEmitterMap.put(profileId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitterMap.remove(profileId));

        sseEmitter.onTimeout(()-> {
            sseEmitterMap.remove(profileId);
            log.info("timeout profileId:{}", profileId);
        });

        sseEmitter.onError(e -> {
            sseEmitterMap.remove(profileId);
            log.error("sse 오류 발생 profileId:{}", profileId, e);
        });

        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("connected"));//더미코드를 보냄.
            //503에러를 막고자 함.
        } catch (IOException e) {
            sseEmitterMap.remove(profileId);
            log.error("sse connect 오류 발생 ", e);
        }

        return sseEmitter;
    }

    public void sendNotification(Long profileId, String message) {
        SseEmitter sseEmitter = sseEmitterMap.get(profileId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name("notification").data(message));

            } catch (IOException e) {
                sseEmitterMap.remove(profileId);
                log.error("error sending notification to profileId:{}", profileId, e);
            }
        } else {
            log.warn("not found sse profileId:{}", profileId);
        }
    }
}
