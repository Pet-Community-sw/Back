package com.example.PetApp.service.sse;

import com.example.PetApp.domain.Member;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseEmitterManager {

    private final MemberRepository memberRepository;

    private final JwtTokenizer jwtTokenizer;

    private final static Long DEFAULT_TIMEOUT = 60 * 60 * 1000L;

    private final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();//스레드 중복 방지

    @Transactional(readOnly = true)
    public SseEmitter subscribe(String token) {
        String email = jwtTokenizer.parseAccessToken(token).getSubject();
        Member member = memberRepository.findByEmail(email).get();

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        sseEmitterMap.put(member.getMemberId(), sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitterMap.remove(member.getMemberId()));

        sseEmitter.onTimeout(() -> {
            sseEmitterMap.remove(member.getMemberId());
            log.info("timeout memberId:{}", member.getMemberId());
        });

        sseEmitter.onError(e -> {
            sseEmitterMap.remove(member.getMemberId());
            log.error("sse 오류 발생 memberId:{}", member.getMemberId(), e);
        });

        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("connected"));//더미코드를 보냄.
            //503에러를 막고자 함.
        } catch (IOException e) {
            sseEmitterMap.remove(member.getMemberId());
            log.error("sse connect 오류 발생 ", e);
        }

        return sseEmitter;
    }

    public void sendNotification(Long memberId, String message) {
        SseEmitter sseEmitter = sseEmitterMap.get(memberId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name("notification").data(message));

            } catch (IOException e) {
                sseEmitterMap.remove(memberId);
                log.error("error sending notification to memberId:{}", memberId, e);
            }
        } else {
            log.warn("not found sse memberId:{}", memberId);
        }
    }
}
