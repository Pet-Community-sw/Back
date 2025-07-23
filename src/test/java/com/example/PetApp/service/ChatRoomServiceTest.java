package com.example.PetApp.service;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.groupchat.ChatRoomsResponseDto;
import com.example.PetApp.dto.groupchat.UpdateChatRoomDto;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ChatRoomMapper;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.mongo.ChatMessageRepository;
import com.example.PetApp.service.chatroom.ChatRoomServiceImpl;
import com.example.PetApp.service.chatting.ChattingReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomServiceImpl;

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChattingReader chattingReader;

    @Mock private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("getChatRooms_성공")
    void test1() {
        // given
        Long profileId = 1L;
        Profile profile = Profile.builder()
                .profileId(profileId)
                .petName("초코")
                .build();

        WalkingTogetherPost walkingTogetherPost = WalkingTogetherPost.builder()
                .walkingTogetherPostId(100L)
                .profile(Profile.builder().profileId(1L).build())
                .build();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(99L)
                .profiles(List.of(profile))
                .name("테스트방")
                .walkingTogetherPost(walkingTogetherPost)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(chatRoomRepository.findAllByProfilesContains(profile)).thenReturn(List.of(chatRoom));

        when(valueOperations.get("chat:lastMessage99")).thenReturn("안녕!");
        when(valueOperations.get("chat:lastMessageTime99")).thenReturn(LocalDateTime.now().toString());
        when(valueOperations.get("unRead:99:" + profileId)).thenReturn("3");

        // when
        List<ChatRoomsResponseDto> result = chatRoomServiceImpl.getChatRooms(profileId);

        // then
        assertThat(result).hasSize(1);
        ChatRoomsResponseDto dto = result.get(0);
        assertThat(dto.getChatRoomId()).isEqualTo(99L);
        assertThat(dto.getLastMessage()).isEqualTo("안녕!");
        assertThat(dto.getUnReadCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getChatRooms_profil을 찾지 못하는 경우_실패")
    void test2() {
        //given
        Long profileId = 1L;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.getChatRooms(profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한이 없습니다.");

    }

    @Test
    @DisplayName("createChatRoom_채팅방 새로 성공_성공")
    void test3() {
        // given
        Profile profile = Profile.builder()
                .profileId(10L)
                .petName("초이선자이")
                .build();

        WalkingTogetherPost post = WalkingTogetherPost.builder()
                .walkingTogetherPostId(1L)
                .limitCount(5)
                .profile(profile)
                .build();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(2L)
                .build();

        when(chatRoomRepository.findByWalkingTogetherPost(post)).thenReturn(Optional.empty());
        try(MockedStatic<ChatRoomMapper> mockedStatic = mockStatic(ChatRoomMapper.class)) {
            mockedStatic.when(()->ChatRoomMapper.toEntity(post, profile)).thenReturn(chatRoom);
        }
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        // when
        CreateChatRoomResponseDto result = chatRoomServiceImpl.createChatRoom(post, profile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(2L);
        assertThat(result.isCreated()).isTrue();

        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("createChatRoom_이미 채팅방이 있을 때 참여_성공")
    void test4() {
        // given
        Profile profile = Profile.builder()
                .profileId(10L)
                .petName("초이선자이")
                .build();

        WalkingTogetherPost post = WalkingTogetherPost.builder()
                .walkingTogetherPostId(1L)
                .profile(profile)
                .limitCount(5)
                .build();

        ChatRoom existingRoom = ChatRoom.builder()
                .chatRoomId(100L)
                .profiles(new ArrayList<>()) // 현재 참여자 0명
                .build();

        when(chatRoomRepository.findByWalkingTogetherPost(post)).thenReturn(Optional.of(existingRoom));

        // when
        CreateChatRoomResponseDto result = chatRoomServiceImpl.createChatRoom(post, profile);

        // then
        assertThat(result.getChatRoomId()).isEqualTo(100L);
        assertThat(result.isCreated()).isFalse();
    }

    @Test
    @DisplayName("createChatRoom_인원이 초과할 경우_실패")
    void test5() {
        //given
        Profile profile = Profile.builder()
                .profileId(10L)
                .petName("초이선자이")
                .build();

        Profile fakeProfile = Profile.builder()
                .profileId(11L)
                .petName("멍멍이")
                .build();

        WalkingTogetherPost post = WalkingTogetherPost.builder()
                .walkingTogetherPostId(1L)
                .profile(fakeProfile)
                .limitCount(1)
                .build();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(1L)
                .profiles(List.of(fakeProfile))
                .build();

        when(chatRoomRepository.findByWalkingTogetherPost(post)).thenReturn(Optional.of(chatRoom));

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.createChatRoom(post, profile))
                .isInstanceOf(ConflictException.class)
                .hasMessage("인원초과");
        assertThat(chatRoom.getProfiles()).hasSize(1);

    }

    @Test
    @DisplayName("getProfiles_성공")
    void test6() {
        //given
        Long chatRoomId = 1L;

        Profile profile = Profile.builder().profileId(1L).build();

        Profile profile1 = Profile.builder().profileId(2L).build();


        ChatRoom chatRoom=ChatRoom.builder()
                .chatRoomId(1L)
                .profiles(List.of(profile1, profile))
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        //when
        List<Long> result = chatRoomServiceImpl.getProfiles(chatRoomId);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(1L, 2L);
    }

    @Test
    @DisplayName("getProfiles_채팅방이 없을 경우_실패")
    void test7() {
        //given
        Long chatRoomId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.getProfiles(chatRoomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("채팅방 없습니다.");
    }

    @Test
    @DisplayName("deleteChatRoom_성공")
    void test8() {
        //given
        Long chatRoomId = 1L;
        Long profileId = 1L;

        Profile profile = Profile.builder()
                .profileId(1L)
                .build();
        Profile profile1 = Profile.builder()
                .profileId(2L)
                .build();


        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(1L)
                .profiles(new ArrayList<>(List.of(profile1, profile)))//remove가 있어서 가변으로 만들어야함.
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        //when
        chatRoomServiceImpl.deleteChatRoom(chatRoomId, profileId);

        //then
        verify(chatMessageRepository).deleteByChatRoomId(chatRoomId);
        verify(chatRoomRepository).deleteById(chatRoomId);
    }

    @Test
    @DisplayName("deleteChatRoom_해당 채팅방에 속해있지 않은 경우_실패")
    void test9() {
        //given
        Long chatRoomId = 1L;
        Long profileId = 3L;

        Profile profile = Profile.builder()
                .profileId(1L)
                .build();

        Profile profile1 = Profile.builder()
                .profileId(2L)
                .build();

        Profile profile3 = Profile.builder()
                .profileId(3L)
                .build();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(1L)
                .profiles(new ArrayList<>(List.of(profile1, profile)))//remove가 있어서 가변으로 만들어야함.
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile3));

        //when&then
        assertThatThrownBy(() -> chatRoomServiceImpl.deleteChatRoom(chatRoomId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("deleteChatRoom_해당 채팅방이 없는 경우_실패")
    void test10() {
        //given
        Long chatRoomId = 1L;
        Long profileId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.deleteChatRoom(chatRoomId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 채팅방은 없습니다.");
    }

    @Test
    @DisplayName("deleteChatRoom_프로필 등록하지 않은 경우_실패")
    void test11() {
        //given
        Long chatRoomId = 1L;
        Long profileId = 1L;

        ChatRoom chatRoom = ChatRoom.builder().chatRoomId(1L).build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.deleteChatRoom(chatRoomId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 등록해주세요.");
    }

    @Test
    @DisplayName("updateChatRoom_성공")
    void test12() {
        //given
        Long chatRoomId = 1L;

        Long profileId = 1L;

        UpdateChatRoomDto updateChatRoomDto = UpdateChatRoomDto.builder()
                .chatRoomName("멍멍")
                .limitCount(3)
                .build();

        Profile profile = Profile.builder()
                .profileId(1L)
                .petName("멍멍")
                .build();

        WalkingTogetherPost walkingTogetherPost = WalkingTogetherPost.builder()
                .walkingTogetherPostId(1L)
                .profile(profile)
                .build();

        ChatRoom chatRoom=ChatRoom.builder()
                .chatRoomId(1L)
                .name("냐옹")
                .limitCount(5)
                .walkingTogetherPost(walkingTogetherPost)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        //when
        chatRoomServiceImpl.updateChatRoom(chatRoomId, updateChatRoomDto, profileId);

        //then
        assertThat(chatRoom.getName()).isEqualTo("멍멍");
        assertThat(chatRoom.getLimitCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("updateChatRoom_해당 채팅방이 없는 경우_실패")
    void test13() {
        //given
        Long chatRoomId = 1L;

        Long profileId = 1L;

        UpdateChatRoomDto updateChatRoomDto = UpdateChatRoomDto.builder()
                .chatRoomName("멍멍")
                .limitCount(3)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.updateChatRoom(chatRoomId, updateChatRoomDto, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 채팅방은 없습니다.");

    }

    @Test
    @DisplayName("updateChatRoom_프로필 등록하지 않은 경우_실패")
    void test14() {
        //given
        Long chatRoomId = 1L;

        Long profileId = 1L;

        UpdateChatRoomDto updateChatRoomDto = UpdateChatRoomDto.builder()
                .chatRoomName("멍멍")
                .limitCount(3)
                .build();

        ChatRoom chatRoom=ChatRoom.builder()
                .chatRoomId(1L)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.updateChatRoom(chatRoomId, updateChatRoomDto, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 등록해주세요.");

    }

    @Test
    @DisplayName("updateChatRoom_게시물 작성자가 아닌 유저가 수정하려는 경우_실패")
    void test15() {
        //given
        Long chatRoomId = 1L;

        Long profileId = 2L;


        UpdateChatRoomDto updateChatRoomDto = UpdateChatRoomDto.builder()
                .chatRoomName("멍멍")
                .limitCount(3)
                .build();

        Profile profile = Profile.builder()
                .profileId(1L)
                .petName("멍멍")
                .build();

        Profile fakeProfile = Profile.builder()
                .profileId(2L)
                .petName("멍멍")
                .build();

        WalkingTogetherPost walkingTogetherPost = WalkingTogetherPost.builder()
                .walkingTogetherPostId(1L)
                .profile(profile)
                .build();

        ChatRoom chatRoom=ChatRoom.builder()
                .chatRoomId(1L)
                .name("냐옹")
                .limitCount(5)
                .walkingTogetherPost(walkingTogetherPost)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(fakeProfile));

        //when & then
        assertThatThrownBy(() -> chatRoomServiceImpl.updateChatRoom(chatRoomId, updateChatRoomDto, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("수정 권한이 없습니다.");
    }
}

