package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.GetUpdatePostResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImp implements PostService {

    @Value("${spring.dog.post.image.upload}")
    private String postUploadDir;

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public List<PostListResponseDto> getPosts(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Post> posts = postRepository.findByOrderByRegdateDesc(pageable).getContent();
        return posts.stream().map(post->new PostListResponseDto(
                post.getPostId(),
                post.getPostImageUrl(),
                post.getProfile().getProfileId(),
                post.getProfile().getDogName(),
                post.getProfile().getImageUrl(),
                post.getTitle(),
                getTimeAgo(post.getRegdate()),
                post.getViewCount(),
                post.getLikeCount()
                )).collect(Collectors.toList());
    }

    @Transactional
    @Override//카테고리도 해야됨.
    public ResponseEntity<Object> createPost(PostDto createPostDto)  {
        MultipartFile file = createPostDto.getPostImageFile();
        Optional<Profile> profile1 = profileRepository.findById(createPostDto.getProfileId());
        if (profile1.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지않는 profile입니다.");
        }
        String imageFileName = "";
        imageFileName = fileSetting(createPostDto, file, imageFileName);
        Profile profile = profile1.get();

            Post post = Post.builder()
                    .profile(profile)
                    .content(createPostDto.getContent())
                    .title(createPostDto.getTitle())
                    .postImageUrl(imageFileName)
                    .build();
            Post newPost = postRepository.save(post);
            return ResponseEntity.ok(newPost.getPostId());

    }

    @Transactional
    @Override//comment도 추가시켜야됨.
    public ResponseEntity<Object> getPost(Long postId, String email) {
        Optional<Post> post = postRepository.findById(postId);
        Member member = memberRepository.findByEmail(email).get();
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없는 게시물입니다.");
        }
        GetUpdatePostResponseDto getPostResponseDto = getPostResponseDto(member, post);

        return ResponseEntity.ok(getPostResponseDto);
    }



    @Transactional
    @Override//principal을 profileId로 바꿔보자
    public ResponseEntity<String> deletePost(Long postId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없는 게시물입니다.");
        }
        if (post.get().getProfile().getMemberId().equals(member.getMemberId())) {
            postRepository.deleteById(postId);
            return ResponseEntity.ok("삭제 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
    }

    @Transactional
    @Override//사진을 안넣었을 때 null이어야하고, profileid, createAt시간
    public ResponseEntity<Object> updatePost(Long postId, PostDto updatePostDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없는 게시물입니다.");
        }
        if (post.get().getProfile().getMemberId().equals(member.getMemberId())&&post.get().getProfile().getProfileId().equals(updatePostDto.getProfileId())) {
            String imageFileName="";
            imageFileName = fileSetting(updatePostDto, updatePostDto.getPostImageFile(), imageFileName);

            post.get().setPostImageUrl(imageFileName);
            post.get().setTitle(updatePostDto.getTitle());
            post.get().setContent(updatePostDto.getContent());

            GetUpdatePostResponseDto updatePostResponseDto = getPostResponseDto(member, post);
            return ResponseEntity.ok(updatePostResponseDto);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
    }

    private String fileSetting(PostDto createPostDto, MultipartFile file, String imageFileName) {
        try {
            if (!(createPostDto.getPostImageFile().isEmpty())) {
                UUID uuid = UUID.randomUUID();
                imageFileName = "/post/" + uuid + "_" + file.getOriginalFilename();
                Path path = Paths.get(postUploadDir, imageFileName);
                Files.copy(file.getInputStream(), path);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return imageFileName;
    }

    private GetUpdatePostResponseDto getPostResponseDto(Member member, Optional<Post> post) {
        List<GetCommentsResponseDto> comments = post.get().getComments().stream().map(
                comment -> new GetCommentsResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getLikeCount(),
                        comment.getPostId(),
                        comment.getProfile().getImageUrl(),
                        comment.getProfile().getDogName(),
                        getTimeAgo(comment.getRegdate()),
                        comment.getProfile().getProfileId()
                        )
        ).collect(Collectors.toList());

        GetUpdatePostResponseDto getPostResponseDto = GetUpdatePostResponseDto.builder()
                .postId(post.get().getPostId())
                .title(post.get().getTitle())
                .content(post.get().getContent())
                .postImageUrl(post.get().getPostImageUrl())
                .viewCount(post.get().getViewCount())
                .likeCount(post.get().getLikeCount())
                .profileId(post.get().getProfile().getProfileId())
                .profileName(post.get().getProfile().getDogName())
                .profileImageUrl(post.get().getProfile().getImageUrl())
                .comments(comments)
                .isOwner(false)
                .build();
        if (post.get().getProfile().getMemberId().equals(member.getMemberId())) {
            getPostResponseDto.setOwner(true);
        }
        return getPostResponseDto;
    }

    private String getTimeAgo(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(localDateTime, now);
        long hours = ChronoUnit.HOURS.between(localDateTime, now);
        long days = ChronoUnit.DAYS.between(localDateTime, now);

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
    }
}
