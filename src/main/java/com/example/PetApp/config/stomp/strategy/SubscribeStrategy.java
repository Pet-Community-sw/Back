package com.example.PetApp.config.stomp.strategy;

import com.example.PetApp.config.stomp.SubscribeInfo;
import com.example.PetApp.domain.*;
import com.example.PetApp.repository.jpa.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribeStrategy implements StompCommandStrategy {

    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final WalkRecordRepository walkRecordRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String subscriptionId = accessor.getSubscriptionId();
        Principal user = accessor.getUser();

        log.info("[STOMP] SUBSCRIBE 요청 - destination: {}, subscriptionId: {}", destination, subscriptionId);

        if (destination == null || user == null) {
            throw new IllegalArgumentException("destination 또는 user 정보가 없습니다.");
        }

        if (destination.startsWith("/sub/chat/")) {
            handleGroupChat(accessor, destination, subscriptionId, user);
        } else if (destination.startsWith("/sub/member/chat/")) {
            handleOneToOneChat(accessor, destination, subscriptionId, user);
        } else if (destination.startsWith("/sub/walk-record/location/")) {
            handleWalkRecord(accessor, destination, user);
        } else {
            log.error("[STOMP] 알 수 없는 구독 경로: {}", destination);
            throw new IllegalArgumentException("알 수 없는 구독 경로입니다.");
        }
    }

    private void handleGroupChat(StompHeaderAccessor accessor, String destination, String subscriptionId, Principal user) {
        Long chatRoomId = Long.valueOf(destination.substring("/sub/chat/".length()));
        String profileId = user.getName();

        Profile profile = profileRepository.findById(Long.valueOf(profileId))
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

        if (!chatRoomRepository.existsByChatRoomIdAndProfilesContains(chatRoomId, profile)) {
            log.error("[STOMP] chatRoomId:{} 접근 권한 없는 profileId:{}", chatRoomId, profileId);
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        redisTemplate.opsForSet().add("chatRoomId:" + chatRoomId + ":onlineProfiles", profileId);
        redisTemplate.opsForValue().set("session:" + accessor.getSessionId(), chatRoomId.toString());

        SubscribeInfo info = new SubscribeInfo(chatRoomId, profileId, ChatMessage.ChatRoomType.MANY);
        try {
            String json = objectMapper.writeValueAsString(info);
            redisTemplate.opsForValue().set("subscriptionId:" + subscriptionId, json);
        } catch (JsonProcessingException e) {
            log.error("[STOMP] group chat subscribeInfo 직렬화 실패", e);
            throw new RuntimeException("subscribeInfo 직렬화 실패", e);
        }
        log.info("[STOMP] group chat 구독 성공 - chatRoomId: {}, profileId: {}", chatRoomId, profileId);
    }

    private void handleOneToOneChat(StompHeaderAccessor accessor, String destination, String subscriptionId, Principal user) {
        Long memberChatRoomId = Long.valueOf(destination.substring("/sub/member/chat/".length()));
        String memberId = user.getName();

        MemberChatRoom room = memberChatRoomRepository.findById(memberChatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("1:1 채팅방이 존재하지 않습니다."));

        boolean hasAccess = room.getMembers().stream()
                .anyMatch(m -> m.getMemberId().equals(Long.valueOf(memberId)));

        if (!hasAccess) {
            log.error("[STOMP] memberChatRoomId:{} 접근 권한 없는 memberId:{}", memberChatRoomId, memberId);
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        redisTemplate.opsForSet().add("memberChatRoomId:" + memberChatRoomId + ":onlineMembers", memberId);
        redisTemplate.opsForValue().set("session:" + accessor.getSessionId(), memberChatRoomId.toString());

        SubscribeInfo info = new SubscribeInfo(memberChatRoomId, memberId, ChatMessage.ChatRoomType.ONE);
        try {
            String json = objectMapper.writeValueAsString(info);
            redisTemplate.opsForValue().set("subscriptionId:" + subscriptionId, json);
        } catch (JsonProcessingException e) {
            log.error("[STOMP] 1:1 chat subscribeInfo 직렬화 실패", e);
            throw new RuntimeException("subscribeInfo 직렬화 실패", e);
        }
        log.info("[STOMP] 1:1 chat 구독 성공 - memberChatRoomId: {}, memberId: {}", memberChatRoomId, memberId);
    }

    private void handleWalkRecord(StompHeaderAccessor accessor, String destination, Principal user) {
        Long walkRecordId = Long.valueOf(destination.substring("/sub/walk-record/location/".length()));
        String memberId = user.getName();

        WalkRecord walkRecord = walkRecordRepository.findById(walkRecordId)
                .orElseThrow(() -> new IllegalArgumentException("산책기록이 존재하지 않습니다."));

        if (!walkRecord.getDelegateWalkPost().getProfile().getMember().getMemberId().equals(Long.valueOf(memberId))) {
            log.error("[STOMP] walkRecordId:{} 접근 권한 없는 memberId:{}", walkRecordId, memberId);
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        log.info("[STOMP] walk-record 위치 구독 성공 - walkRecordId: {}, memberId: {}", walkRecordId, memberId);
    }
}
