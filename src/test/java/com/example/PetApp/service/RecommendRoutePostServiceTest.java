//package com.example.PetApp.service;
//
//import com.example.PetApp.domain.Member;
//import com.example.PetApp.domain.post.RecommendRoutePost;
//import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
//import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostResponseDto;
//import com.example.PetApp.dto.recommendroutepost.GetRecommendRoutePostsResponseDto;
//import com.example.PetApp.exception.NotFoundException;
//import com.example.PetApp.mapper.RecommendRoutePostMapper;
//import com.example.PetApp.query.MemberQueryService;
//import com.example.PetApp.repository.jpa.MemberRepository;
//import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
//import com.example.PetApp.service.like.LikeService;
//import com.example.PetApp.service.post.recommend.RecommendRoutePostServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.redis.core.BoundSetOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class RecommendRoutePostServiceTest {
//
//    @InjectMocks
//    private RecommendRoutePostServiceImpl recommendRoutePostService;
//
//    @Mock
//    private RecommendRoutePostRepository recommendRoutePostRepository;
//    @Mock
//    private MemberRepository memberRepository;
//    @Mock
//    private LikeService likeService;
//    @Mock
//    private MemberQueryService memberQueryService;
//    @Mock
//    private RedisTemplate<String, Long> likeRedisTemplate;
//
//    CreateRecommendRoutePostDto createRecommendRoutePostDto;
//    Member member;
//    String email;
//
//    @BeforeEach
//    void setUp() {
//        member = Member.builder()
//                .memberId(1L)
//                .name("sunjae")
//                .email("chltjswo")
//                .build();
//
//        email = "chltjswo";
//
//        createRecommendRoutePostDto = CreateRecommendRoutePostDto.builder()
//                .title("하남시 산책길 추천")
//                .content("산책길")
//                .locationName("오브제")
//                .locationLongitude(12.23)
//                .locationLatitude(24.33)
//                .build();
//    }
//
//    @Test
//    @DisplayName("createRecommendRoutePost_성공")
//    void createRecommendRoutePost_success() {
//        //given
//        when(memberQueryService.findByMember(email)).thenReturn(member);
//        when(recommendRoutePostRepository.save(any(RecommendRoutePost.class))).thenAnswer(invocation -> {
//            RecommendRoutePost savedPost = invocation.getArgument(0);
//            savedPost.setPostId(100L);
//            return savedPost;
//        });
//
//        //when
//        CreateRecommendRoutePostResponseDto responseDto = recommendRoutePostService.createRecommendRoutePost(createRecommendRoutePostDto, email);
//
//        //then
//        assertThat(responseDto.getRecommendRoutePostId()).isEqualTo(100L);
//        verify(memberQueryService).findByMember(email);
//        verify(recommendRoutePostRepository).save(any(RecommendRoutePost.class));
//    }
//
//    @Test
//    @DisplayName("createRecommendRoutePost_실패_회원을 찾을 수 없는경우")
//    void createRecommendRoutePost_fail_memberNotFound() {
//        //given
//        when(memberQueryService.findByMember(anyString())).thenThrow(new NotFoundException("해당 유저는 없습니다."));
//
//        //when&then
//        assertThatThrownBy(() -> recommendRoutePostService.createRecommendRoutePost(createRecommendRoutePostDto, email))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessage("해당 유저는 없습니다.");
//
//        verify(memberQueryService).findByMember(email);
//    }
//
//
//}
