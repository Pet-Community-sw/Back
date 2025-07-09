package com.example.PetApp.service.chatting;

import com.example.PetApp.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRedisCleaner {

    private final StringRedisTemplate stringRedisTemplate;

    public void cleanChatRedis(ChatMessage chatMessage, Long id) {
        stringRedisTemplate.delete("chat:lastMessage" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("chat:lastMessageTime" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("unReadChat:" + chatMessage.getChatRoomId() + ":" + id);
        log.info("Redis 정리 완료 (MANY): {}", chatMessage.getChatRoomId());
    }

    public void cleanMemberChatRedis(ChatMessage chatMessage, Long id) {
        stringRedisTemplate.delete("memberChat:lastMessage" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("memberChat:lastMessageTime" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("unReadMemberChat:" + chatMessage.getChatRoomId() + ":" + id);
        log.info("Redis 정리 완료 (ONE): {}", chatMessage.getChatRoomId());
    }

    public void redisDeleteUnreadKey(Long chatRoomId, Long userId, ChatMessage.ChatRoomType chatRoomType) {
        String unreadKey = makeUnreadKey(chatRoomId, userId, chatRoomType);
        stringRedisTemplate.delete(unreadKey);
    }

    public String makeUnreadKey(Long chatRoomId, Long userId, ChatMessage.ChatRoomType chatRoomType) {
        switch (chatRoomType) {
            case MANY -> {
                return "unReadChatCount:" + chatRoomId + ":" + userId;
            }
            case ONE -> {
                return "unReadMemberChatCount:" + chatRoomId + ":" + userId;
            }
            default -> {
                throw new IllegalArgumentException("지원하지 않는 채팅방 타입입니다.");
            }
        }
    }
}
