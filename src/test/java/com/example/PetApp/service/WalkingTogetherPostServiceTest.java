package com.example.PetApp.service;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.GetWalkingTogetherPostResponseDto;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.*;
import com.example.PetApp.service.chat.ChatRoomService;
import com.example.PetApp.service.walkingtogetherpost.WalkingTogetherPostServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalkingTogetherPostServiceTest {
    @InjectMocks
    private WalkingTogetherPostServiceImp walkingTogetherPostServiceImp;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private WalkingTogetherPostRepository walkingTogetherPostRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private PetBreedRepository petBreedRepository;
    @Mock
    private RecommendRoutePostRepository recommendRoutePostRepository;

    @Test
    @DisplayName("getWalkingTogetherPost_성공")
    void test1() {
        //given
        Long walkingTogetherPostId = 1L;
        Long profileId = 1L;
        PetBreed petBreed = PetBreed.builder()
                .petBreedId(1L)
                .name("리")
                .build();

        Profile profile = Profile.builder()
                .profileId(1L)
                .avoidBreeds(Set.of(petBreed))
                .build();

        Profile fakeProfile = Profile.builder()
                .profileId(2L)
                .build();
        WalkingTogetherPost walkingTogetherPost = WalkingTogetherPost.builder()
                .walkingTogetherPostId(1L)
                .profile(profile)
                .scheduledTime(LocalDateTime.now())
                .profiles(Set.of(2L))
                .limitCount(2)
                .walkingTogetherPostTime(LocalDateTime.now())
                .avoidBreeds(Set.of(1L))
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(walkingTogetherPost));
        when(petBreedRepository.findByName(any())).thenReturn(Optional.of(petBreed));


        //when
        GetWalkingTogetherPostResponseDto result = walkingTogetherPostServiceImp.getWalkingTogetherPost(walkingTogetherPostId, profileId);

        //then
        assertThat(result.getWalkingTogetherPostId()).isEqualTo(1L);
        assertThat(result.isOwner()).isTrue();
        assertThat(result.isFiltering()).isTrue();
    }

    @Test
    @DisplayName("getWalkingTogetherPost_프로필이 존재하지 않는 경우_실패")
    void test2() {
        // given
        Long walkingTogetherPostId = 1L;
        Long profileId = 10L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.getWalkingTogetherPost(walkingTogetherPostId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정 해주세요.");

    }

    @Test
    @DisplayName("getWalkingTogetherPost_함께 산책해요 게시글이 없는 경우_실패")
    void test3() {
        // given
        Long walkingTogetherPostId = 1L;
        Long profileId = 10L;

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("푸들")
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findById(walkingTogetherPostId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.getWalkingTogetherPost(walkingTogetherPostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 함께 산책해요 게시글은 없습니다.");
    }

    @Test
    @DisplayName("getWalkingTogetherPosts_성공")
    void test4() {
        // given
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("푸들")
                .build();

        RecommendRoutePost recommendRoutePost = RecommendRoutePost.builder()
                .recommendRouteId(recommendRoutePostId)
                .member(Member.builder().memberId(1L).build())
                .title("추천 산책길")
                .build();

        PetBreed petBreed = PetBreed.builder()
                .petBreedId(100L)
                .name("푸들")
                .build();

        WalkingTogetherPost post1 = WalkingTogetherPost.builder()
                .walkingTogetherPostId(101L)
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .walkingTogetherPostTime(LocalDateTime.now())
                .scheduledTime(LocalDateTime.now())
                .build();

        WalkingTogetherPost post2 = WalkingTogetherPost.builder()
                .walkingTogetherPostId(102L)
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .walkingTogetherPostTime(LocalDateTime.now())
                .scheduledTime(LocalDateTime.now())
                .build();

        List<WalkingTogetherPost> postList = List.of(post1, post2);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));
        when(petBreedRepository.findByName("푸들")).thenReturn(Optional.of(petBreed));
        when(walkingTogetherPostRepository.findAllByRecommendRoutePost(recommendRoutePost)).thenReturn(postList);

        // when
        List<GetWalkingTogetherPostResponseDto> result =
                walkingTogetherPostServiceImp.getWalkingTogetherPosts(recommendRoutePostId, profileId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getWalkingTogetherPostId()).isEqualTo(101L);
        assertThat(result.get(1).getWalkingTogetherPostId()).isEqualTo(102L);

        verify(profileRepository).findById(profileId);
        verify(recommendRoutePostRepository).findById(recommendRoutePostId);
        verify(petBreedRepository).findByName("푸들");
        verify(walkingTogetherPostRepository).findAllByRecommendRoutePost(recommendRoutePost);
    }

    @Test
    @DisplayName("getWalkingTogetherPosts_프로필이 없는 경우_실패")
    void test5() {
        //given
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.getWalkingTogetherPosts(recommendRoutePostId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정 해주세요.");
    }

    @Test
    @DisplayName("getWalkingTogetherPosts_산책길 추천 게시글이 없는 경우_실패")
    void test6() {
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("말티즈")
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walkingTogetherPostServiceImp.getWalkingTogetherPosts(recommendRoutePostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책길 추천 게시글은 없습니다.");
    }

    @Test
    @DisplayName("createWalkingTogetherPost_성공")
    void test7() {
        // given
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        CreateWalkingTogetherPostDto dto = CreateWalkingTogetherPostDto.builder()
                .recommendRoutePostId(recommendRoutePostId)
                .scheduledTime(LocalDateTime.now().plusDays(1))
                .limitCount(3)
                .build();

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("푸들")
                .build();

        RecommendRoutePost recommendRoutePost = RecommendRoutePost.builder()
                .recommendRouteId(recommendRoutePostId)
                .title("추천 산책로")
                .build();

        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.save(any(WalkingTogetherPost.class))).thenAnswer(invocation -> {
            WalkingTogetherPost walkingTogetherPost = invocation.getArgument(0);
            return walkingTogetherPost.toBuilder().walkingTogetherPostId(100L).build();
        });

        // when
        CreateWalkingTogetherPostResponseDto result =
                walkingTogetherPostServiceImp.createWalkingTogetherPost(dto, profileId);

        // then
        assertThat(result).isNotNull();
//        assertThat(result.getWalkingTogetherPostId()).isEqualTo(100L);

        verify(recommendRoutePostRepository).findById(recommendRoutePostId);
        verify(profileRepository).findById(profileId);
        verify(walkingTogetherPostRepository).save(any(WalkingTogetherPost.class));
    }

    @Test
    @DisplayName("createWalkingTogetherPost_프로필이 없는 경우_실패")
    void test8() {
        //given
        Long profileId = 1L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.createWalkingTogetherPost(any(CreateWalkingTogetherPostDto.class), profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정 해주세요.");
    }

    @Test
    @DisplayName("createWalkingTogetherPost_산책길 추천 게시글이 없는 경우_실패")
    void test9() {
        // given
        Long profileId = 1L;

        Profile profile = Profile.builder()
                .profileId(1L)
                .build();

        CreateWalkingTogetherPostDto createWalkingTogetherPostDto=CreateWalkingTogetherPostDto.builder()
                .recommendRoutePostId(1L)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(recommendRoutePostRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.createWalkingTogetherPost(createWalkingTogetherPostDto, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책길 추천 게시글은 없습니다.");

    }

    @Test
    @DisplayName("startMatch_성공")
    void test10() {
        // given
        Long walkingTogetherPostId = 1L;
        Long profileId = 2L;

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("리트리버")
                .build();

        WalkingTogetherPost post = WalkingTogetherPost.builder()
                .walkingTogetherPostId(walkingTogetherPostId)
                .profiles(new HashSet<>()) // 아직 매칭 안 된 상태
                .avoidBreeds(new HashSet<>()) // 피해야 할 종 없음
                .build();

        PetBreed petBreed = PetBreed.builder()
                .petBreedId(99L)
                .name("리트리버")
                .build();

        CreateChatRoomResponseDto chatRoomResponseDto =
                new CreateChatRoomResponseDto(1L, true);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));
        when(petBreedRepository.findByName("리트리버")).thenReturn(Optional.of(petBreed));
        when(chatRoomService.createChatRoom(post, profile)).thenReturn(chatRoomResponseDto);

        // when
        CreateChatRoomResponseDto result = walkingTogetherPostServiceImp.startMatch(walkingTogetherPostId, profileId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        verify(chatRoomService).createChatRoom(post, profile);
    }

    @Test
    @DisplayName("startMatch_프로필 없는 경우_실패")
    void test11() {
        //given
        when(profileRepository.findById(2L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.startMatch(anyLong(), 2L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정해주세요.");

    }

    @Test
    @DisplayName("startMatch_함께 산책해요 게시글 없는 경우_실패")
    void test12() {
        //given
        Long profileId = 2L;
        Long walkingTogetherPostId = 1L;

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("푸들")
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findById(walkingTogetherPostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 함께 산책해요 게시글은 없습니다.");
    }


    @Test
    @DisplayName("startMatch_이미 채팅방에 들어가있는 경우_실패")
    void test13() {
        //given
        Long profileId = 2L;
        Long walkingTogetherPostId = 1L;
        Set<Long> profiles = new HashSet<>();
        profiles.add(profileId);

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("푸들")
                .build();

        WalkingTogetherPost post = WalkingTogetherPost.builder()
                .profiles(profiles)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 채팅방에 들어가있습니다.");

    }

    @Test
    @DisplayName("startMatch_견종을 찾을 수 없는 경우_실패")
    void test14() {
        //given
        Long walkingTogetherPostId = 1L;
        Long profileId = 2L;

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("불독")
                .build();

        WalkingTogetherPost post = WalkingTogetherPost.builder()
                .profiles(new HashSet<>())
                .avoidBreeds(new HashSet<>())
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));
        when(petBreedRepository.findByName("불독")).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 견종을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("startMatch_피해야하는 견종일 경우_실패")
    void test15() {
        //when
        Long profileId = 2L;
        Long walkingTogetherPostId = 1L;
        Set<Long> profiles = new HashSet<>();
        profiles.add(3L);

        Profile profile = Profile.builder()
                .profileId(profileId)
                .petBreed("도베르만")
                .build();

        PetBreed petBreed = PetBreed.builder()
                .petBreedId(3L)
                .name("도베르만")
                .build();

        WalkingTogetherPost post = WalkingTogetherPost.builder()
                .profiles(new HashSet<>())
                .avoidBreeds(profiles) // 피해야 할 종에 포함됨
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));
        when(petBreedRepository.findByName("도베르만")).thenReturn(Optional.of(petBreed));

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImp.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("해당 종은 참여할 수 없습니다.");
    }

}