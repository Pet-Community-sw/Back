package com.example.PetApp.service.post.normal;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.NormalPost;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.like.LikeCountDto;
import com.example.PetApp.dto.post.CreatePostResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.GetPostResponseDto;
import com.example.PetApp.dto.post.PostResponseDto;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.PostMapper;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.NormalPostRepository;
import com.example.PetApp.util.imagefile.FileUploadUtil;
import com.example.PetApp.util.imagefile.FileImageKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class NormalNormalPostServiceImpl implements NormalPostService {

    @Value("${spring.dog.post.image.upload}")
    private String postUploadDir;

    private final NormalPostRepository normalPostRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final RedisTemplate<String, Long> likeRedisTemplate;

    @Transactional(readOnly = true)
    @Override
    public List<PostResponseDto> getPosts(int page, String email) {
        log.info("getPosts 요청 : {}", email);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 유저가 없습니다."));
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "postId"));
        List<NormalPost> normalPosts = normalPostRepository.findAll(pageRequest).getContent();

        return PostMapper.toPostListResponseDto(normalPosts, getLikedPostIds(member, normalPosts));
    }


    @Transactional
    @Override
    public CreatePostResponseDto createPost(PostDto createPostDto, String email)  {
        log.info("createPost 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        String imageFileName = FileUploadUtil.fileUpload(createPostDto.getPostImageFile(), postUploadDir, FileImageKind.POST);
        NormalPost normalPost = PostMapper.toEntity(createPostDto, imageFileName, member);
        NormalPost savedPost = normalPostRepository.save(normalPost);
        likeRedisTemplate.opsForSet().add("member:likes:" + savedPost.getPostId(), -1L);
        return new CreatePostResponseDto(savedPost.getPostId());
    }

    @Transactional
    @Override
    public GetPostResponseDto getPost(Long postId, String email) {
        log.info("getPost 요청 email : {}",email);
        NormalPost post = normalPostRepository.findById(postId)
                .orElseThrow(()->new NotFoundException("해당 게시물은 없습니다."));
        Member member = memberRepository.findByEmail(email).get();
        if (!(post.getMember().equals(member))) {//조회수
            post.setViewCount(post.getViewCount()+1);
        }

        return PostMapper.toGetPostResponseDto(post, member, likeRepository.countByPost(post), likeRepository.existsByPostAndMember(post, member));
    }

    @Transactional
    @Override
    public void deletePost(Long postId, String email) {
        log.info("deletePost 요청 email : {}, postId : {}", email, postId);
        Member member = memberRepository.findByEmail(email).get();
        NormalPost post = normalPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
        if (!(post.getMember().equals(member))) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }
        normalPostRepository.deleteById(postId);
        likeRedisTemplate.opsForSet().remove("member:likes:" + postId);
    }

    @Transactional
    @Override
    public void updatePost(Long postId, PostDto updatePostDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        NormalPost post = normalPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
        if (!(post.getMember().equals(member))) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }
        String imageFileName = FileUploadUtil.fileUpload(updatePostDto.getPostImageFile(),
                postUploadDir,
                FileImageKind.POST);

        post.setPostImageUrl(imageFileName);
        post.setContent(new Content(updatePostDto.getTitle(), updatePostDto.getContent()));
    }

    private Set<Long> getLikedPostIds(Member member, List<Post> posts) {
        return new HashSet<>(likeRepository.findLikedPostIds(member, posts));
    }
}

