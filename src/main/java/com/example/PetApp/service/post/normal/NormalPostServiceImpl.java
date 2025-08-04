package com.example.PetApp.service.post.normal;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.NormalPost;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.post.CreatePostResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.GetPostResponseDto;
import com.example.PetApp.dto.post.PostResponseDto;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.mapper.PostMapper;
import com.example.PetApp.query.MemberQueryService;
import com.example.PetApp.query.NormalPostQueryService;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.NormalPostRepository;
import com.example.PetApp.service.like.LikeService;
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
public class NormalPostServiceImpl implements NormalPostService {

    @Value("${spring.dog.post.image.upload}")
    private String postUploadDir;

    private final NormalPostRepository normalPostRepository;
    private final LikeRepository likeRepository;
    private final LikeService likeService;
    private final MemberQueryService memberQueryService;
    private final NormalPostQueryService normalPostQueryService;
    private final RedisTemplate<String, Long> likeRedisTemplate;

    @Transactional(readOnly = true)
    @Override
    public List<PostResponseDto> getPosts(int page, String email) {
        log.info("getPosts 요청 : {}", email);
        Member member = memberQueryService.findByMember(email);
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "postId"));
        List<NormalPost> normalPosts = normalPostRepository.findAll(pageRequest).getContent();
        Set<Long> members = likeRedisTemplate.opsForSet().members("member:likes:" + member.getMemberId());
        return PostMapper.toPostListResponseDto(normalPosts, likeService.getLikeCountMap(normalPosts),members);
    }

    @Transactional
    @Override
    public GetPostResponseDto getPost(Long postId, String email) {
        log.info("getPost 요청 email : {}",email);
        Member member = memberQueryService.findByMember(email);
        NormalPost normalPost = normalPostQueryService.findByNormalPost(postId);
        if (!(normalPost.getMember().equals(member))) {//조회수
            normalPost.setViewCount(normalPost.getViewCount()+1);
        }

        return PostMapper.toGetPostResponseDto(normalPost, member, likeRepository.countByPost(normalPost), likeRepository.existsByPostAndMember(normalPost, member));
    }

    @Transactional
    @Override
    public CreatePostResponseDto createPost(PostDto createPostDto, String email)  {
        log.info("createPost 요청 email : {}", email);
        Member member = memberQueryService.findByMember(email);
        String imageFileName = FileUploadUtil.fileUpload(createPostDto.getPostImageFile(), postUploadDir, FileImageKind.POST);
        NormalPost normalPost = PostMapper.toEntity(createPostDto, imageFileName, member);
        NormalPost savedPost = normalPostRepository.save(normalPost);
        return new CreatePostResponseDto(savedPost.getPostId());
    }

    @Transactional
    @Override
    public void deletePost(Long postId, String email) {
        log.info("deletePost 요청 email : {}, postId : {}", email, postId);
        Member member = memberQueryService.findByMember(email);
        NormalPost normalPost = normalPostQueryService.findByNormalPost(postId);
        if (!(normalPost.getMember().equals(member))) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }
        normalPostRepository.deleteById(postId);
    }

    @Transactional
    @Override
    public void updatePost(Long postId, PostDto updatePostDto, String email) {
        Member member = memberQueryService.findByMember(email);
        NormalPost normalPost = normalPostQueryService.findByNormalPost(postId);
        if (!(normalPost.getMember().equals(member))) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }
        String imageFileName = FileUploadUtil.fileUpload(updatePostDto.getPostImageFile(),
                postUploadDir,
                FileImageKind.POST);

        normalPost.setPostImageUrl(imageFileName);
        normalPost.setContent(new Content(updatePostDto.getTitle(), updatePostDto.getContent()));
    }

}

