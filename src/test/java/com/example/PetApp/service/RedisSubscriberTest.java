package com.example.PetApp.service;

import com.example.PetApp.config.redis.RedisSubscriber;
import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.groupchat.UpdateChatRoomList;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisSubscriberTest {

    @InjectMocks
    private RedisSubscriber redisSubscriber;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private MemberChatRoomRepository memberChatRoomRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private SetOperations<String, String> setOperations;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        redisSubscriber = new RedisSubscriber(
                simpMessagingTemplate,
                objectMapper,
                redisTemplate,
                chatRoomRepository,
                memberChatRoomRepository
        );
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    @DisplayName("RedisSubscriber 그룹채팅 메시지 수신_성공")
    void testSendMessage_GroupChat() throws Exception {
        // given
        Long chatRoomId = 1L;
        Long senderId = 10L;
        Member member = Member.builder().memberId(20L).build();
        Profile senderProfile = Profile.builder().profileId(senderId).petName("초코").member(member).build();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .profiles(List.of(senderProfile))
                .build();

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .chatRoomId(chatRoomId)
                .chatRoomType(ChatMessage.ChatRoomType.MANY)
                .messageType(ChatMessage.MessageType.TALK)
                .message("안녕하세요!")
                .messageTime(LocalDateTime.now())
                .build();

        String json = objectMapper.writeValueAsString(chatMessage);

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(setOperations.members(anyString())).thenReturn(Set.of());

        // when
        redisSubscriber.sendMessage(json);

        // then
        verify(simpMessagingTemplate).convertAndSend(eq("/sub/chat/1"), any(ChatMessage.class));
        verify(redisTemplate.opsForValue()).set(contains("chat:lastMessage"), eq("안녕하세요!"));
        verify(simpMessagingTemplate).convertAndSend(eq("/sub/chat/update"), any(UpdateChatRoomList.class));
    }
}

