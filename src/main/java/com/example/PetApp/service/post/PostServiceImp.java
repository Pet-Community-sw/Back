package com.example.PetApp.service.post;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.GetUpdatePostResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PostServiceImp implements PostService {

    @Value("${spring.dog.post.image.upload}")
    private String postUploadDir;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    @Transactional
    @Override
    public List<PostListResponseDto> getPosts(int page, String email) {
        Member member = memberRepository.findByEmail(email).get();

        log.info("게시물 리스트 요청");
        Pageable pageable = PageRequest.of(page, 10);
        List<Post> posts = postRepository.findByOrderByPostTimeDesc(pageable).getContent();
        TimeAgoUtil timeAgoUtil = new TimeAgoUtil();
        return posts.stream().map(post->new PostListResponseDto(
                post.getPostId(),
                post.getPostImageUrl(),
                post.getMember().getMemberId(),
                post.getMember().getName(),
                post.getMember().getMemberImageUrl(),
                timeAgoUtil.getTimeAgo(post.getPostTime()),
                post.getViewCount(),
                likeRepository.countByPost(post),
                post.getTitle(),
                likeRepository.existsByPostAndMember(post, member)
                )).collect(Collectors.toList());
    }

    @Transactional
    @Override//카테고리도 해야됨.
    public ResponseEntity<?> createPost(PostDto createPostDto, String email)  {
        log.info("게시물 작성 요청");
        log.info("email : {}",email);
        MultipartFile file = createPostDto.getPostImageFile();
        Member member = memberRepository.findByEmail(email).get();
        String imageFileName = "";
        imageFileName = fileSetting(createPostDto, file, imageFileName);

            Post post = Post.builder()
                    .content(createPostDto.getContent())
                    .title(createPostDto.getTitle())
                    .postImageUrl(imageFileName)
                    .member(member)
                    .build();
            Post newPost = postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("postId",newPost.getPostId()));

    }

    @Transactional
    @Override//memberId받아야 할듯?
    public ResponseEntity<?> getPost(Long postId, String email) {
        Optional<Post> post = postRepository.findById(postId);
        Member member = memberRepository.findByEmail(email).get();
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        if (!(post.get().getMember().equals(member))) {//조회수
            post.get().setViewCount(post.get().getViewCount()+1);
        }
        GetUpdatePostResponseDto getPostResponseDto = getPostResponseDto(post.get(), member);

        return ResponseEntity.ok(getPostResponseDto);

    }



    @Transactional
    @Override
    public ResponseEntity<String> deletePost(Long postId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        if (post.get().getMember().equals(member)) {
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
        if (post.get().getMember().equals(member)) {
            String imageFileName="";
            imageFileName = fileSetting(updatePostDto, updatePostDto.getPostImageFile(), imageFileName);

            post.get().setPostImageUrl(imageFileName);
            post.get().setTitle(updatePostDto.getTitle());
            post.get().setContent(updatePostDto.getContent());

            return ResponseEntity.ok().body("수정 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
    }

    private String fileSetting(PostDto createPostDto, MultipartFile file, String imageFileName) {//별도의 파일 분리해야할듯.
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

    private GetUpdatePostResponseDto getPostResponseDto(Post post, Member member) {
        TimeAgoUtil timeAgoUti = new TimeAgoUtil();
        List<GetCommentsResponseDto> comments = post.getComments().stream().map(
                comment -> new GetCommentsResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getLikeCount(),
                        comment.getMember().getMemberId(),
                        comment.getMember().getName(),
                        comment.getMember().getMemberImageUrl(),
                        timeAgoUti.getTimeAgo(comment.getCommentTime()),
                        checkOwner(comment,comment.getMember())
                )
        ).collect(Collectors.toList());

        GetUpdatePostResponseDto getPostResponseDto = GetUpdatePostResponseDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImageUrl(post.getPostImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(likeRepository.countByPost(post))
                .memberId(post.getMember().getMemberId())
                .memberName(post.getMember().getName())
                .memberImageUrl(post.getMember().getMemberImageUrl())
                .comments(comments)
                .createdAt(timeAgoUti.getTimeAgo(post.getPostTime()))
                .like(likeRepository.existsByPostAndMember(post, member))
                .build();

        if (post.getMember().equals(member)) {
            getPostResponseDto.setOwner(true);
        }

        return getPostResponseDto;
    }

    private boolean checkOwner(Comment comment, Member member) {

        if (comment.getMember().equals(member)) {
            return true;
        } else {
            return false;
        }
    }

}

