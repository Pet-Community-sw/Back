package com.example.PetApp.service;

import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.post.CreatePostDto;
import com.example.PetApp.dto.post.GetPostResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.projection.PostProjection;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    @Override
    public List<PostListResponseDto> getPosts(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Post> posts = postRepository.findByOrderByRegdateDesc(pageable).getContent();
        return posts.stream().map(post->new PostListResponseDto(
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
    @Override
    public ResponseEntity<?> createPost(CreatePostDto createPostDto)  {
        MultipartFile file = createPostDto.getPostImageFile();
        Optional<Profile> profile1 = profileRepository.findById(createPostDto.getProfileId());
        if (profile1.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지않는 profile입니다.");
        }
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + file.getOriginalFilename();

        try {
            Path path = Paths.get(postUploadDir, imageFileName);
            Files.copy(file.getInputStream(), path);

            Profile profile = profile1.orElseThrow(() -> new RuntimeException("존재하지 않는 프로필"));

            Post post = Post.builder()
                    .profile(profile)
                    .content(createPostDto.getContent())
                    .title(createPostDto.getTitle())
                    .imageUrl("/post/"+imageFileName)
                    .build();

            Post newPost = postRepository.save(post);
            return ResponseEntity.ok(newPost.getPostId());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Transactional
    @Override//comment도 추가시켜야됨.
    public ResponseEntity<GetPostResponseDto> getPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<PostListResponseDto> comments = post.get().getComments().stream().map(
                comment -> new PostListResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getProfile().getImageUrl(),
                        getTimeAgo(comment.getRegdate()),
                        comment.getProfile().getDogName(),
                        comment.getLikeCount(),
                        comment.getPostId()
                )
        ).collect(Collectors.toList());

        GetPostResponseDto getPostResponseDto=GetPostResponseDto.builder()
                .profileId(post.get().getPostId())
                .title(post.get().getTitle())
                .content(post.get().getContent())
                .imageUrl(post.get().getImageUrl())
                .viewCount(post.get().getViewCount())
                .likeCount(post.get().getLikeCount())
                .postId(post.get().getPostId())
                .profileName(post.get().getProfile().getDogName())
                .profileImageUrl(post.get().getProfile().getImageUrl())
                .comments(comments)
                .build();

        return ResponseEntity.ok(getPostResponseDto);
    }

    private String getTimeAgo(LocalDateTime createdTime) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(createdTime, now);
        long hours = ChronoUnit.HOURS.between(createdTime, now);
        long days = ChronoUnit.DAYS.between(createdTime, now);

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
