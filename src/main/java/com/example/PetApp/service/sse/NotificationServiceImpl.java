package com.example.PetApp.service.sse;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.notification.NotificationListDto;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.util.TimeAgoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final ObjectMapper objectMapper;
    private final SseEmitterManager sseEmitterManager;


    @Transactional(readOnly = true)
    @Override
    public List<NotificationListDto> getNotifications(String email) {//몇분 전 추가해야할듯.
        Member member = memberRepository.findByEmail(email).get();
        Set<String> keys = notificationRedisTemplate.keys("notifications:" + member.getMemberId() + ":*");
        return keys.stream()
                .map(key -> notificationRedisTemplate.opsForValue().get(key))
                .map(message -> {
                    try {
                        NotificationListDto notificationListDto = objectMapper.readValue((String) message, NotificationListDto.class);
                        notificationListDto.setCreatedAt(TimeAgoUtil.getTimeAgo(notificationListDto.getNotificationTime()));
                        return notificationListDto;
                        //readValue는 String,class로 기대를함. 그래서 명시적(String)으로 형변환.
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

    }

    @Override
    public SseEmitter subscribe(String token) {
        return sseEmitterManager.subscribe(token);
    }
}
