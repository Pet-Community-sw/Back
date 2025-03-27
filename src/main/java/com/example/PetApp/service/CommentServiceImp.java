package com.example.PetApp.service;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.commment.CreateCommentDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.repository.CommentRepository;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import com.example.PetApp.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public ResponseEntity<Object> createComment(CreateCommentDto createCommentDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Profile> profile = profileRepository.findById(createCommentDto.getProfileId());
        Optional<Post> post = postRepository.findById(createCommentDto.getPostId());
        if (profile.isEmpty()||post.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 프로필 혹은 해당 게시물이 없습니다.");
        }
        if (!(profile.get().getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.badRequest().body("일치하지 않는 사용자입니다.");
        }
        Comment comment=Comment.builder()
                .content(createCommentDto.getContent())
                .postId(createCommentDto.getPostId())
                .likeCount(createCommentDto.getLikeCount())
                .profile(profile.get())
                .build();
        Comment newComment = commentRepository.save(comment);
        return ResponseEntity.ok(newComment.getCommentId());
    }

    @Transactional
    @Override
    public ResponseEntity<Object> getComment(Long commentId, String email) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<Member> member = memberRepository.findByEmail(email);
        TimeAgoUtil timeAgoUtil = new TimeAgoUtil();
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없는 댓글입니다.");
        }
        GetCommentsResponseDto getCommentsResponseDto = GetCommentsResponseDto.builder()
                .commentId(commentId)
                .content(comment.get().getContent())
                .profileId(comment.get().getProfile().getProfileId())
                .profileDogName(comment.get().getProfile().getDogName())
                .profileImageUrl(comment.get().getProfile().getImageUrl())
                .postId(comment.get().getPostId())
                .likeCount(comment.get().getLikeCount())
                .createdAt(timeAgoUtil.getTimeAgo(comment.get().getRegdate()))
                .build();
        if (comment.get().getProfile().getMemberId().equals(member.get().getMemberId())) {
            getCommentsResponseDto.setOwner(true);
        }
        return ResponseEntity.ok(getCommentsResponseDto);
    }

}
