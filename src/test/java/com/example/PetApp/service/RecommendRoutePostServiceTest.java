package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostResponseDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendRoutePostsResponseDto;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.RecommendRoutePostMapper;
import com.example.PetApp.query.QueryService;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.service.like.LikeService;
import com.example.PetApp.service.post.recommend.RecommendRoutePostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendRoutePostServiceTest {

    @InjectMocks
    private RecommendRoutePostServiceImpl recommendRoutePostService;

    @Mock
    private RecommendRoutePostRepository recommendRoutePostRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LikeService likeService;
    @Mock
    private QueryService queryService;
    @Mock
    private RedisTemplate<String, Long> likeRedisTemplate;

    CreateRecommendRoutePostDto createRecommendRoutePostDto;
    Member member;
    String email;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .memberId(1L)
                .name("sunjae")
                .email("chltjswo")
                .build();

        email = "chltjswo";

        createRecommendRoutePostDto = CreateRecommendRoutePostDto.builder()
                .title("하남시 산책길 추천")
                .content("산책길")
                .locationName("오브제")
                .locationLongitude(12.23)
                .locationLatitude(24.33)
                .build();
    }

    @Test
    @DisplayName("createRecommendRoutePost_성공")
    void createRecommendRoutePost_success() {
        //given
        when(queryService.findByMember(email)).thenReturn(member);
        when(recommendRoutePostRepository.save(any(RecommendRoutePost.class))).thenAnswer(invocation -> {
            RecommendRoutePost savedPost = invocation.getArgument(0);
            savedPost.setPostId(100L);
            return savedPost;
        });

        //when
        CreateRecommendRoutePostResponseDto responseDto = recommendRoutePostService.createRecommendRoutePost(createRecommendRoutePostDto, email);

        //then
        assertThat(responseDto.getRecommendRoutePostId()).isEqualTo(100L);
        verify(queryService).findByMember(email);
        verify(recommendRoutePostRepository).save(any(RecommendRoutePost.class));
    }

    @Test
    @DisplayName("createRecommendRoutePost_실패_회원을 찾을 수 없는경우")
    void createRecommendRoutePost_fail_memberNotFound() {
        //given
        when(queryService.findByMember(anyString())).thenThrow(new NotFoundException("해당 유저는 없습니다."));

        //when&then
        assertThatThrownBy(() -> recommendRoutePostService.createRecommendRoutePost(createRecommendRoutePostDto, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 유저는 없습니다.");

        verify(queryService).findByMember(email);
    }

    @Test
    @DisplayName("getRecommendRoutePosts_성공")
    void getRecommendRoutePosts_success() {
        // Given
        List<RecommendRoutePost> posts = List.of(
                RecommendRoutePost.builder().postId(101L).build(),
                RecommendRoutePost.builder().postId(102L).build()
        );

        Page<RecommendRoutePost> postPage = new PageImpl<>(posts);

        Set<Long> likedPostIds = Set.of(101L);
        Map<Long, Long> likeCountMap = Map.of(101L, 5L, 102L, 2L);

        // Mapper 결과 (Stub)
        List<GetRecommendRoutePostsResponseDto> responseDtos = List.of(
                GetRecommendRoutePostsResponseDto.builder()
                        .recommendRoutePostId(101L)
                        .title("title1")
                        .likeCount(5L)
                        .isLike(true)
                        .build(),
                GetRecommendRoutePostsResponseDto.builder()
                        .recommendRoutePostId(102L)
                        .title("title2")
                        .likeCount(2L)
                        .isLike(false)
                        .build()
        );

        // Mocking
        when(queryService.findByMember(email)).thenReturn(member);
        when(recommendRoutePostRepository.findByRecommendRoutePostByLocation(anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(Pageable.class)))
                .thenReturn(postPage);

        // BoundSetOperations 선언 및 Mock
        BoundSetOperations<String, Long> boundSetOperations = mock(BoundSetOperations.class);
        when(likeRedisTemplate.boundSetOps(anyString())).thenReturn(boundSetOperations);
        when(boundSetOperations.members()).thenReturn(likedPostIds);

        when(likeService.getLikeCountMap(posts)).thenReturn(likeCountMap);

        try (MockedStatic<RecommendRoutePostMapper> mockedMapper = mockStatic(RecommendRoutePostMapper.class)) {
            mockedMapper.when(() ->
                    RecommendRoutePostMapper.toRecommendRoutePostsList(posts, likeCountMap, likedPostIds, member)
            ).thenReturn(responseDtos);

            // When
            List<GetRecommendRoutePostsResponseDto> result = recommendRoutePostService.getRecommendRoutePosts(1.0, 1.0, 2.0, 2.0, 0, email);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getRecommendRoutePostId()).isEqualTo(101L);
            assertThat(result.get(0).isLike()).isTrue();
            assertThat(result.get(1).getRecommendRoutePostId()).isEqualTo(102L);
            assertThat(result.get(1).isLike()).isFalse();

            verify(queryService).findByMember(email);
            verify(recommendRoutePostRepository).findByRecommendRoutePostByLocation(anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(Pageable.class));
            verify(likeRedisTemplate).opsForSet();
            verify(boundSetOperations).members("member:likes:" + member.getMemberId());
            verify(likeService).getLikeCountMap(posts);
        }
    }
}
