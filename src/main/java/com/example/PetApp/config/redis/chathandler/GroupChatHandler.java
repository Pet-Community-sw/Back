package com.example.PetApp.config.redis.chathandler;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ChatMessageMapper;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GroupChatHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    public void handle(ChatMessage message) {
        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId())
                .orElseThrow(() -> new NotFoundException("해당 채팅방이 없습니다."));

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoom.getChatRoomId(), message);

        saveLastMessageToRedis(message, "chat:lastMessage", "chat:lastMessageTime");

        Set<String> onlineProfiles = redisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoom.getChatRoomId() + ":onlineProfiles");

        Map<Long, Long> unReadMap = countUnread(chatRoom, message, onlineProfiles);

        messagingTemplate.convertAndSend("/sub/chat/update",
                ChatMessageMapper.toUpdateChatRoomList(chatRoom.getChatRoomId(), message, unReadMap));
    }

    private void saveLastMessageToRedis(ChatMessage message, String key, String timeKey) {
        redisTemplate.opsForValue().set(key + message.getChatRoomId(), message.getMessage());
        redisTemplate.opsForValue().set(timeKey + message.getChatRoomId(), String.valueOf(message.getMessageTime()));
    }

    private Map<Long, Long> countUnread(ChatRoom chatRoom, ChatMessage message, Set<String> onlineProfiles) {
        Map<Long, Long> map = new HashMap<>();
        for (Profile profile : chatRoom.getProfiles()) {
            if (!profile.getProfileId().equals(message.getSenderId())) {
                boolean isOnline = onlineProfiles != null && onlineProfiles.contains(profile.getProfileId().toString());
                if (!isOnline) {
                    String key = "unReadChatCount:" + message.getChatRoomId() + ":" + profile.getProfileId();
                    Long count = redisTemplate.opsForValue().increment(key);
                    map.put(profile.getMember().getMemberId(), count);
                }
            }
        }
        return map;
    }
}

