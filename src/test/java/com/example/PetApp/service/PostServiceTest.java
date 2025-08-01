package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.post.CreatePostResponseDto;
import com.example.PetApp.dto.post.GetPostResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.service.post.PostServiceImpl;
import com.example.PetApp.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LikeRepository likeRepository;

    Member member = Mapper.createFakeMember();

    PostDto postDto = Mapper.toPostDto();

    @Test
    @DisplayName("createPost_성공")
    void test1() {

        //given
        String email = "chltjswo789@naver.com";


        when(memberRepository.findByEmail("chltjswo789@naver.com")).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(Post.builder().postId(100L).build());
//        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
//            Post fakePost = invocation.getArgument(0);
//            return fakePost.toBuilder().postId(100L).build();
//        });
        //when
        CreatePostResponseDto result = postServiceImpl.createPost(postDto, email);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getPostId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("getPost_성공")
    void test2() {
        //given
        Member fakeMember = Mapper.createFakeMember(1L, "chltjswo789@naver.com");
        Member view = Mapper.createFakeMember(2L, "dlwlsh789@naver.com");

        Post post = Post.builder()
                .postId(100L)
                .member(fakeMember)
                .content(new Content("산책 대행 1", "내용 1"))
                .viewCount(0L)
                .comments(new ArrayList<>())
                .build();

        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(memberRepository.findByEmail("dlwlsh789@naver.com")).thenReturn(Optional.of(view));
        when(likeRepository.countByPost(post.getPostId())).thenReturn(6L);
        when(likeRepository.existsByPostAndMember(post, view)).thenReturn(true);

        //when
        GetPostResponseDto fakePost = postServiceImpl.getPost(100L, "dlwlsh789@naver.com");

        //then
        assertThat(fakePost).isNotNull();
        assertThat(fakePost.getPostResponseDto().getViewCount()).isEqualTo(1L);//조회수 증가 확인
        assertThat(fakePost.getPostResponseDto().getLikeCount()).isEqualTo(6L);//좋아요 개수 확인
    }

    @Test
    @DisplayName("updatePost_성공")
    void test3() {
        //given
        Long postId = 1L;
        String email = "chltjswo789@naver.com";

        Post post = Post.builder()
                .postId(1L)
                .content(new Content("산책 대행 1", "내용 1"))
                .member(member)
                .postImageUrl(null)
                .comments(new ArrayList<>())
                .build();


        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        //when
        postServiceImpl.updatePost(postId, postDto, email);

        //then
        assertThat(post.getContent().getContent()).isEqualTo("b");
    }

    @Test
    @DisplayName("updatePost_실패")
    void test4() {
        //given
        Long postId = 1L;
        String email = "chltjswo789@naver.com";
        Member fakeMember = Mapper.createFakeMember(1L, "dlwlsh1708@naver.com");
        Member member1 = Mapper.createFakeMember(2L, "chltjswo79@naver.com");

        Post post = Post.builder()
                .postId(1L)
                .content(new Content("산책 대행 1", "내용 1"))
                .member(fakeMember)
                .postImageUrl(null)
                .comments(new ArrayList<>())
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member1));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        //when
        assertThatThrownBy(() -> postServiceImpl.updatePost(postId, Mapper.toPostDto(), email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("수정 권한이 없습니다.");

    }
}
