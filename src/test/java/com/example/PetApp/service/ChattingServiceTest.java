package com.example.PetApp.service;

import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.service.chatroom.ChatRoomService;
import com.example.PetApp.service.chatting.ChattingService;
import com.example.PetApp.util.SendNotificationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChattingServiceTest {

    @InjectMocks
    private ChattingService chattingService;

    @Mock
    private RedisPublisher redisPublisher;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private MemberChatRoomRepository memberChatRoomRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SendNotificationUtil sendNotificationUtil;
    @Mock
    private SetOperations<String, String> setOperations;

    @Test
    @DisplayName("sendToMessage_MANY_TALK_성공")
    void test1() {
        // given
        Long senderId = 1L;
        Long chatRoomId = 99L;

        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .chatRoomId(chatRoomId)
                .chatRoomType(ChatMessage.ChatRoomType.MANY)
                .messageType(ChatMessage.MessageType.TALK)
                .build();

        Profile profile = Profile.builder()
                .profileId(senderId)
                .petName("멍멍이")
                .petImageUrl("url")
                .member(Member.builder().memberId(100L).build())
                .build();

        Profile anotherProfile = Profile.builder()
                .profileId(senderId)
                .petName("멍멍이")
                .petImageUrl("url")
                .member(Member.builder().memberId(10L).build())
                .build();

        ChatRoom room = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .profiles(List.of(profile, anotherProfile))
                .build();

        when(profileRepository.findById(senderId)).thenReturn(Optional.of(profile));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(room));
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForSet().members(anyString())).thenReturn(Set.of());

        // when
        chattingService.sendToMessage(message, senderId);

        // then
        verify(redisPublisher, times(1)).publish(any(ChatMessage.class));
    }

    @Test
    @DisplayName("sendMessage_MANY_ENTER_성공")
    void testEnterMessage_MANY() {
        // given
        Long senderId = 1L;
        Long chatRoomId = 101L;

        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .chatRoomId(chatRoomId)
                .chatRoomType(ChatMessage.ChatRoomType.MANY)
                .messageType(ChatMessage.MessageType.ENTER)
                .build();

        Member member = Member.builder().memberId(10L).build();
        Profile senderProfile = Profile.builder()
                .profileId(senderId)
                .petName("초코")
                .petImageUrl("img.png")
                .member(member)
                .build();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .profiles(List.of(senderProfile))
                .build();

        // Mock 설정
        when(profileRepository.findById(senderId)).thenReturn(Optional.of(senderProfile));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(Set.of());

        // when
        chattingService.sendToMessage(message, senderId);

        // then
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(redisPublisher, times(1)).publish(captor.capture());

        ChatMessage publishedMessage = captor.getValue();
        assertThat(publishedMessage.getMessage()).isEqualTo("초코님이 입장하셨습니다.");
        assertThat(publishedMessage.getMessageType()).isEqualTo(ChatMessage.MessageType.ENTER);
    }



}

