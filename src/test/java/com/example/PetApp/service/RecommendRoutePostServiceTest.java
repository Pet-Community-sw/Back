package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostResponseDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendRoutePostsResponseDto;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.service.recommendroutepost.RecommendRoutePostServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendRoutePostServiceTest {

    @InjectMocks
    private RecommendRoutePostServiceImp recommendRoutePostServiceImp;

    @Mock
    private RecommendRoutePostRepository recommendRoutePostRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LikeRepository likeRepository;

    @Test
    @DisplayName("createRecommendPost_성공")
    void test1() {
        //given
        String email = "chltjswo789@naver.com";

        Member member = Member.builder()
                .memberId(1L)
                .build();

        CreateRecommendRoutePostDto createRecommendRoutePostDto = CreateRecommendRoutePostDto.builder()
                .title("a")
                .content("a")
                .locationLatitude(2.2)
                .locationLongitude(2.2)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.save(any(RecommendRoutePost.class))).thenAnswer(invocation -> {
            RecommendRoutePost post = invocation.getArgument(0);
            post.setRecommendRouteId(10L);
            return post;
        });

        //when
        CreateRecommendRoutePostResponseDto result = recommendRoutePostServiceImp.createRecommendRoutePost(createRecommendRoutePostDto, email);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getRecommendRoutePostId()).isEqualTo(10L);

    }

    @Test
    @DisplayName("getRecommendRoutePosts_성공")
    void testGetRecommendRoutePosts() {
        // given
        String email = "chltjswo789@naver.com";
        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .build();

        RecommendRoutePost post1 = RecommendRoutePost.builder()
                .recommendRouteId(1L)
                .member(member)
                .title("산책로 1")
                .content("좋아요")
                .locationLongitude(127.01)
                .locationLatitude(37.55)
                .recommendRouteTime(LocalDateTime.now())
                .build();

        RecommendRoutePost post2 = RecommendRoutePost.builder()
                .recommendRouteId(2L)
                .member(member)
                .title("산책로 2")
                .content("좋아요")
                .locationLongitude(127.02)
                .locationLatitude(37.56)
                .recommendRouteTime(LocalDateTime.now())
                .build();

        List<RecommendRoutePost> posts = new ArrayList<>();
        posts.add(post2);
        posts.add(post1);
        Page<RecommendRoutePost> fakePage = new PageImpl<>(posts);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findByRecommendRoutePostByLocation(anyDouble(), anyDouble(), anyDouble(), any(), any(Pageable.class))).thenReturn(fakePage);
        when(likeRepository.findLikedRecommendIds(member, posts)).thenReturn(List.of(1L));

        //when
        List<GetRecommendRoutePostsResponseDto> result = recommendRoutePostServiceImp.getRecommendRoutePosts(127.0, 128.0, 37.0, 38.0, 0, email);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("산책로 2");
    }

}
