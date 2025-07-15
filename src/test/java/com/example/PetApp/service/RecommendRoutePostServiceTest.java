package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.embedded.Location;
import com.example.PetApp.domain.embedded.PostContent;
import com.example.PetApp.dto.recommendroutepost.*;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
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
import org.springframework.test.util.ReflectionTestUtils;

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
        when(recommendRoutePostRepository.save(any(RecommendRoutePost.class))).thenReturn(RecommendRoutePost.builder().recommendRouteId(10L).build());
//        when(recommendRoutePostRepository.save(any(RecommendRoutePost.class))).thenAnswer(invocation -> {
//            RecommendRoutePost post = invocation.getArgument(0);
//            return post.toBuilder().recommendRouteId(10L).build();
//        });

        //when
        CreateRecommendRoutePostResponseDto result = recommendRoutePostServiceImp.createRecommendRoutePost(createRecommendRoutePostDto, email);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getRecommendRoutePostId()).isEqualTo(10L);

    }

    @Test
    @DisplayName("getRecommendRoutePosts_by-location_성공")
    void test2() {
        // given
        String email = "chltjswo789@naver.com";
        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .build();

        RecommendRoutePost post1 = RecommendRoutePost.builder()
                .recommendRouteId(1L)
                .member(member)
                .postContent(new PostContent("산책로 1", "좋아요"))
                .location(new Location(127.01, 37.55))
                .build();

        ReflectionTestUtils.setField(post1, "createdAt", LocalDateTime.now());

        RecommendRoutePost post2 = RecommendRoutePost.builder()
                .recommendRouteId(2L)
                .member(member)
                .postContent(new PostContent("산책로 2", "좋아요"))
                .location(new Location(127.02, 37.56))
                .build();

        ReflectionTestUtils.setField(post2, "createdAt", LocalDateTime.now());

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

    @Test
    @DisplayName("getRecommendRoutePosts_by-place_성공")
    void test3() {
        // given
        String email = "chltjswo789@naver.com";
        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .build();

        RecommendRoutePost post1 = RecommendRoutePost.builder()
                .recommendRouteId(1L)
                .member(member)
                .postContent(new PostContent("산책로 1", "좋아요"))
                .location(new Location(127.01, 37.55))
                .build();

        ReflectionTestUtils.setField(post1, "createdAt", LocalDateTime.now());

        RecommendRoutePost post2 = RecommendRoutePost.builder()
                .recommendRouteId(2L)
                .member(member)
                .postContent(new PostContent("산책로 2", "좋아요"))
                .location(new Location(127.02, 37.56))
                .build();

        ReflectionTestUtils.setField(post2, "createdAt", LocalDateTime.now());

        List<RecommendRoutePost> posts = new ArrayList<>();
        posts.add(post2);
        posts.add(post1);
        Page<RecommendRoutePost> fakePage = new PageImpl<>(posts);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findByRecommendRoutePostByPlace(anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(fakePage);
        when(likeRepository.findLikedRecommendIds(member, posts)).thenReturn(List.of(1L));

        //when
        List<GetRecommendRoutePostsResponseDto> result = recommendRoutePostServiceImp.getRecommendRoutePosts(127.02, 37.56, 0, email);

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("getRecommendRoutePost_성공")
    void test4() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";
        Member member = Member.builder()
                .memberId(1L)
                .name("초이")
                .memberImageUrl("abc")
                .build();

        RecommendRoutePost recommendRoutePost=RecommendRoutePost.builder()
                .recommendRouteId(1L)
                .postContent(new PostContent("산책로 1", "좋아요"))
                .member(member)
                .location(new Location(127.02, 37.56))
                .build();

        ReflectionTestUtils.setField(recommendRoutePost, "createdAt", LocalDateTime.now());

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));
        when(likeRepository.countByRecommendRoutePost(any(RecommendRoutePost.class))).thenReturn(3L);
        when(likeRepository.existsByRecommendRoutePostAndMember(any(RecommendRoutePost.class), any(Member.class))).thenReturn(true);

        //when
        GetRecommendPostResponseDto result = recommendRoutePostServiceImp.getRecommendRoutePost(recommendRoutePostId, email);

        //then
        assertThat(result.getRecommendRoutePostId()).isEqualTo(recommendRoutePost.getRecommendRouteId());
        assertThat(result.getTitle()).isEqualTo(recommendRoutePost.getPostContent().getTitle());
        assertThat(result.getContent()).isEqualTo(recommendRoutePost.getPostContent().getContent());
        assertThat(result.getLikeCount()).isEqualTo(3L);
        assertThat(result.isLike()).isTrue();

    }

    @Test
    @DisplayName("getRecommendRoutePost_산책길 추천 게시물이 없을 경우_실패")
    void test5() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";

        Member member = Member.builder()
                .memberId(1L)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> recommendRoutePostServiceImp.getRecommendRoutePost(recommendRoutePostId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책길 추천 게시물은 없습니다.");
    }

    @Test
    @DisplayName("updateRecommendRoutePost_성공")
    void test6() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";

        Member member=Member.builder()
                .memberId(1L)
                .build();

        UpdateRecommendRoutePostDto updateRecommendRoutePostDto = UpdateRecommendRoutePostDto.builder()
                .title("aa")
                .content("bb")
                .build();

        RecommendRoutePost recommendRoutePost=RecommendRoutePost.builder()
                .postContent(new PostContent("산책로 1", "좋아요"))
                .member(member)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));

        //when
        recommendRoutePostServiceImp.updateRecommendRoutePost(recommendRoutePostId, updateRecommendRoutePostDto, email);

        //then
        assertThat(recommendRoutePost.getPostContent().getTitle()).isEqualTo("aa");
        assertThat(recommendRoutePost.getPostContent().getContent()).isEqualTo("bb");
    }

    @Test
    @DisplayName("updateRecommendRoutePost_산책길 추천 게시글이 없는경우_실패")
    void test7() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";

        Member member=Member.builder()
                .memberId(1L)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> recommendRoutePostServiceImp.updateRecommendRoutePost(recommendRoutePostId, any(UpdateRecommendRoutePostDto.class), email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책길 추천 게시글은 없습니다.");
    }

    @Test
    @DisplayName("updateRecommendRoutePost_수정 권한 없는경우_실패")
    void test8() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";

        Member member=Member.builder()
                .memberId(1L)
                .build();

        Member fakeMember=Member.builder()
                .memberId(2L)
                .build();

        RecommendRoutePost recommendRoutePost=RecommendRoutePost.builder()
                .recommendRouteId(1L)
                .member(member)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(fakeMember));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));

        //when & then
        assertThatThrownBy(() -> recommendRoutePostServiceImp.updateRecommendRoutePost(recommendRoutePostId, any(UpdateRecommendRoutePostDto.class), email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("수정 권한이 없습니다.");
    }

    @Test
    @DisplayName("deleteRecommendRoutePost_성공")
    void test9() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";

        Member member=Member.builder()
                .memberId(1L)
                .build();

        RecommendRoutePost recommendRoutePost=RecommendRoutePost.builder()
                .recommendRouteId(1L)
                .member(member)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));

        //when
        recommendRoutePostServiceImp.deleteRecommendRoutePost(recommendRoutePostId, email);

        //then
        verify(recommendRoutePostRepository).deleteById(recommendRoutePostId);

    }

    @Test
    @DisplayName("deleteRecommendRoutePost_산책길 추천 게시글이 없는 경우_실패")
    void test10() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";

        Member member = Member.builder()
                .memberId(1L)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> recommendRoutePostServiceImp.deleteRecommendRoutePost(recommendRoutePostId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책길 추천 게시글은 없습니다.");
    }

    @Test
    @DisplayName("deleteRecommendRoutePost_삭제 권한이 없는 경우_실패")
    void test11() {
        //given
        Long recommendRoutePostId = 1L;
        String email = "test";

        Member member=Member.builder()
                .memberId(1L)
                .build();

        Member fakeMember=Member.builder()
                .memberId(2L)
                .build();

        RecommendRoutePost recommendRoutePost=RecommendRoutePost.builder()
                .recommendRouteId(1L)
                .member(member)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(fakeMember));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));

        //when & then
        assertThatThrownBy(() -> recommendRoutePostServiceImp.deleteRecommendRoutePost(recommendRoutePostId, email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("삭제 권한이 없습니다.");
    }

}
