package com.example.PetApp.config.redis;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.chat.UpdateChatRoomList;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional//lazy 문제
    public void sendMessage(String message) throws JsonProcessingException {
        ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(), chatMessage);

        redisTemplate.opsForValue().set("chat:lastMessage" + chatMessage.getChatRoomId(), chatMessage.getMessage());
        redisTemplate.opsForValue().set("chat:lastMessageTime" + chatMessage.getChatRoomId(), String.valueOf(chatMessage.getMessageTime()));
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId()).orElseThrow(() -> new RuntimeException("채탕방 없음"));
        Map<Long, Long> unReadMap = new HashMap<>();
        for (Profile profile : chatRoom.getProfiles()) {
            if (!(profile.getProfileId().equals(chatMessage.getSenderId()))) {
                String key = "unRead:" + chatMessage.getChatRoomId() + ":" + profile.getProfileId();
                Long count = redisTemplate.opsForValue().increment(key);
                unReadMap.put(profile.getMember().getMemberId(), count);
            }
        }

        UpdateChatRoomList updateChatRoomList = UpdateChatRoomList.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .lastMessage(chatMessage.getMessage())
                .lastMessageTime(chatMessage.getMessageTime())
                .unReadCount(unReadMap)
                .build();

        simpMessagingTemplate.convertAndSend("/sub/chat/update"+chatRoom.getChatRoomId(),updateChatRoomList);
    }
}
