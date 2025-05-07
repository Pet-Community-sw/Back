package com.example.PetApp.config.redis;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.chat.UpdateChatRoomList;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
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
    private final MemberChatRoomRepository memberChatRoomRepository;

    @Transactional//lazy 문제 분리 좀 시켜야됨. 추후에 리팩토링 진행해야됨.
    public void sendMessage(String message) throws JsonProcessingException {
        ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
        Map<Long, Long> unReadMap = new HashMap<>();
        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId()).orElseThrow(() -> new RuntimeException("채탕방 없음"));

            simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(), chatMessage);

            redisTemplate.opsForValue().set("chat:lastMessage" + chatMessage.getChatRoomId(), chatMessage.getMessage());
            redisTemplate.opsForValue().set("chat:lastMessageTime" + chatMessage.getChatRoomId(), String.valueOf(chatMessage.getMessageTime()));
            for (Profile profile : chatRoom.getProfiles()) {
                if (!(profile.getProfileId().equals(chatMessage.getSenderId()))) {
                    String key = "unReadChat:" + chatMessage.getChatRoomId() + ":" + profile.getProfileId();
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
            simpMessagingTemplate.convertAndSend("/sub/chat/update", updateChatRoomList);
        } else {

            MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(chatMessage.getChatRoomId()).orElseThrow(() -> new RuntimeException("채팅방 없음"));
            simpMessagingTemplate.convertAndSend("/sub/member/chat/" + chatMessage.getChatRoomId(), chatMessage);

            redisTemplate.opsForValue().set("memberChat:lastMessage" + chatMessage.getChatRoomId(), chatMessage.getMessage());
            redisTemplate.opsForValue().set("memberChat:lastMessageTime" + chatMessage.getChatRoomId(), String.valueOf(chatMessage.getMessageTime()));

            for (Member member : memberChatRoom.getMembers()) {
                if (!member.getMemberId().equals(chatMessage.getSenderId())) {
                    String key = "unReadMemberChat:" + chatMessage.getChatRoomId() + ":" + member.getMemberId();
                    Long count = redisTemplate.opsForValue().increment(key);
                    unReadMap.put(member.getMemberId(), count);
                }
            }
            UpdateChatRoomList updateChatRoomList = UpdateChatRoomList.builder()
                    .chatRoomId(memberChatRoom.getMemberChatRoomId())
                    .lastMessage(chatMessage.getMessage())
                    .lastMessageTime(chatMessage.getMessageTime())
                    .unReadCount(unReadMap)
                    .build();
            simpMessagingTemplate.convertAndSend("/sub/member/chat/update", updateChatRoomList);

        }
    }
}
