package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.GetUpdatePostResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.repository.LikeRepository;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import com.example.PetApp.util.TimeAgoUtil;
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
import java.util.List;
import java.util.Map;
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
    private final LikeRepository likeRepository;

    @Transactional
    @Override
    public List<PostListResponseDto> getPosts(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Post> posts = postRepository.findByOrderByRegdateDesc(pageable).getContent();
        TimeAgoUtil timeAgoUtil = new TimeAgoUtil();
        return posts.stream().map(post->new PostListResponseDto(
                post.getPostId(),
                post.getPostImageUrl(),
                post.getProfile().getProfileId(),
                post.getProfile().getDogName(),
                post.getProfile().getImageUrl(),
                post.getTitle(),
                timeAgoUtil.getTimeAgo(post.getRegdate()),
                post.getViewCount(),
                likeRepository.countByPostId(post.getPostId())
                )).collect(Collectors.toList());
    }

    @Transactional
    @Override//카테고리도 해야됨.
    public ResponseEntity<Object> createPost(PostDto createPostDto, String email)  {
        Member member = memberRepository.findByEmail(email).get();
        MultipartFile file = createPostDto.getPostImageFile();
        Optional<Profile> profile1 = profileRepository.findById(createPostDto.getProfileId());
        if (profile1.isEmpty()||!(member.getMemberId().equals(profile1.get().getMemberId()))) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
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
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("postId",newPost.getPostId()));

    }

    @Transactional//조회수 중복 허용? 방지?
    public ResponseEntity<Object> getPost(Post post, Member member, boolean isView) {
        if (isView) {//자기 post에 들어가는 것은 조회수안올라감.
            post.setViewCount(post.getViewCount()+1);
        }
        GetUpdatePostResponseDto getPostResponseDto = getPostResponseDto(member, post);

        return ResponseEntity.ok(getPostResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<Object> getPost(Long postId, String email) {
        Optional<Post> post = postRepository.findById(postId);
        Member member = memberRepository.findByEmail(email).get();
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        if (post.get().getProfile().getMemberId().equals(member.getMemberId())) {
            return getPost(post.get(), member, false);
        } else {
            return getPost(post.get(), member, true);
        }
    }



    @Transactional
    @Override//principal을 profileId로 바꿔보자
    public ResponseEntity<String> deletePost(Long postId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        if (post.get().getProfile().getMemberId().equals(member.getMemberId())) {
            postRepository.deleteById(postId);
            return ResponseEntity.ok("삭제 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Object> updatePost(Long postId, PostDto updatePostDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        if (post.get().getProfile().getMemberId().equals(member.getMemberId())&&post.get().getProfile().getProfileId().equals(updatePostDto.getProfileId())) {
            String imageFileName="";
            imageFileName = fileSetting(updatePostDto, updatePostDto.getPostImageFile(), imageFileName);

            post.get().setPostImageUrl(imageFileName);
            post.get().setTitle(updatePostDto.getTitle());
            post.get().setContent(updatePostDto.getContent());

            GetUpdatePostResponseDto updatePostResponseDto = getPostResponseDto(member, post.get());
            return ResponseEntity.ok(updatePostResponseDto);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
    }

    private String fileSetting(PostDto createPostDto, MultipartFile file, String imageFileName) {
        try {
            if (!createPostDto.getPostImageFile().isEmpty()) {
                UUID uuid = UUID.randomUUID();
                imageFileName = uuid + "_" + file.getOriginalFilename(); // 파일명만 저장
                Path path = Paths.get(postUploadDir, imageFileName);
                Files.copy(file.getInputStream(), path);

                return "/post/"+imageFileName;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 저장 중 오류 발생: " + e.getMessage(), e);
        }
        return "";
    }

    private GetUpdatePostResponseDto getPostResponseDto(Member member, Post post) {
        TimeAgoUtil timeAgoUti = new TimeAgoUtil();
        List<GetCommentsResponseDto> comments = post.getComments().stream().map(
                comment -> new GetCommentsResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getLikeCount(),
                        comment.getPostId(),
                        comment.getProfile().getImageUrl(),
                        comment.getProfile().getDogName(),
                        timeAgoUti.getTimeAgo(comment.getRegdate()),
                        comment.getProfile().getProfileId(),
                        comment.getProfile().getMemberId().equals(member.getMemberId())
                        )
        ).collect(Collectors.toList());


        GetUpdatePostResponseDto getPostResponseDto = GetUpdatePostResponseDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImageUrl(post.getPostImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(likeRepository.countByPostId(post.getPostId()))
                .profileId(post.getProfile().getProfileId())
                .profileName(post.getProfile().getDogName())
                .profileImageUrl(post.getProfile().getImageUrl())
                .comments(comments)
                .createdAt(timeAgoUti.getTimeAgo(post.getRegdate()))
                .build();
        if (post.getProfile().getMemberId().equals(member.getMemberId())) {
            getPostResponseDto.setOwner(true);
        }

        return getPostResponseDto;
    }

}

