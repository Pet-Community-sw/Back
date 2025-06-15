package com.example.PetApp.config.redis;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.groupchat.UpdateChatRoomList;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;

    @Transactional
    public void sendMessage(String message) {
        ChatMessage chatMessage = deserializeMessage(message);
        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            handleGroupChatMessage(chatMessage);
        } else {
            handleOneToOneChatMessage(chatMessage);
        }
    }

    private ChatMessage deserializeMessage(String message) {
        try {
            return objectMapper.readValue(message, ChatMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("RedisSubscriber: 메시지 역직렬화 실패", e);
        }
    }

    private void handleGroupChatMessage(ChatMessage chatMessage) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                .orElseThrow(() -> new NotFoundException("해당 채팅방이 없습니다."));

        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(), chatMessage);
        saveLastMessageToRedis("chat:lastMessage", "chat:lastMessageTime", chatMessage);

        Set<String> onlineProfiles = redisTemplate.opsForSet().members(
                "chatRoomId:" + chatRoom.getChatRoomId() + ":onlineProfiles");

        Map<Long, Long> unReadMap = countUnreadForGroup(chatRoom, chatMessage, onlineProfiles);

        sendChatRoomUpdate("/sub/chat/update", chatRoom.getChatRoomId(), chatMessage, unReadMap);
    }

    private void handleOneToOneChatMessage(ChatMessage chatMessage) {
        MemberChatRoom chatRoom = memberChatRoomRepository.findById(chatMessage.getChatRoomId())
                .orElseThrow(() -> new NotFoundException("해당 채팅방이 없습니다."));

        simpMessagingTemplate.convertAndSend("/sub/member/chat/" + chatMessage.getChatRoomId(), chatMessage);
        saveLastMessageToRedis("memberChat:lastMessage", "memberChat:lastMessageTime", chatMessage);

        Set<String> onlineMembers = redisTemplate.opsForSet().members(
                "memberChatRoomId:" + chatRoom.getMemberChatRoomId() + ":onlineMembers");

        Map<Long, Long> unReadMap = countUnreadForMembers(chatRoom, chatMessage, onlineMembers);

        sendChatRoomUpdate("/sub/member/chat/update", chatRoom.getMemberChatRoomId(), chatMessage, unReadMap);
    }

    private void saveLastMessageToRedis(String messageKeyPrefix, String timeKeyPrefix, ChatMessage message) {
        redisTemplate.opsForValue().set(messageKeyPrefix + message.getChatRoomId(), message.getMessage());
        redisTemplate.opsForValue().set(timeKeyPrefix + message.getChatRoomId(), String.valueOf(message.getMessageTime()));
    }

    private Map<Long, Long> countUnreadForGroup(ChatRoom chatRoom, ChatMessage message, Set<String> onlineProfiles) {
        Map<Long, Long> unReadMap = new HashMap<>();
        for (Profile profile : chatRoom.getProfiles()) {
            if (!profile.getProfileId().equals(message.getSenderId())) {
                boolean isOnline = onlineProfiles != null && onlineProfiles.contains(profile.getProfileId().toString());
                if (!isOnline) {
                    String key = "unReadChatCount:" + message.getChatRoomId() + ":" + profile.getProfileId();
                    Long count = redisTemplate.opsForValue().increment(key);
                    unReadMap.put(profile.getMember().getMemberId(), count);
                }
            }
        }
        return unReadMap;
    }

    private Map<Long, Long> countUnreadForMembers(MemberChatRoom chatRoom, ChatMessage message, Set<String> onlineMembers) {
        Map<Long, Long> unReadMap = new HashMap<>();
        for (Member member : chatRoom.getMembers()) {
            if (!member.getMemberId().equals(message.getSenderId())) {
                boolean isOnline = onlineMembers != null && onlineMembers.contains(member.getMemberId().toString());
                if (!isOnline) {
                    String key = "unReadMemberChat:" + message.getChatRoomId() + ":" + member.getMemberId();
                    Long count = redisTemplate.opsForValue().increment(key);
                    unReadMap.put(member.getMemberId(), count);
                }
            }
        }
        return unReadMap;
    }

    private void sendChatRoomUpdate(String destination, Long roomId, ChatMessage message, Map<Long, Long> unReadMap) {
        UpdateChatRoomList update = UpdateChatRoomList.builder()
                .chatRoomId(roomId)
                .lastMessage(message.getMessage())
                .lastMessageTime(message.getMessageTime())
                .unReadCount(unReadMap)
                .build();
        simpMessagingTemplate.convertAndSend(destination, update);
    }
}
