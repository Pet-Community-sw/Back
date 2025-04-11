package com.example.PetApp.service.comment;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.repository.CommentRepository;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import com.example.PetApp.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;

    @Transactional
    @Override
    public ResponseEntity<Object> createComment(CommentDto commentDto, Long profileId) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        Optional<Post> post = postRepository.findById(commentDto.getPostId());
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 요청입니다.");
        }
        Comment comment=Comment.builder()
                .content(commentDto.getContent())
                .post(post.get())
                .profile(profile.get())
                .build();
        Comment newComment = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment.getCommentId());
    }

    @Transactional
    @Override
    public ResponseEntity<Object> getComment(Long commentId, Long profileId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없는 댓글입니다.");
        }
        TimeAgoUtil timeAgoUtil = new TimeAgoUtil();
        GetCommentsResponseDto getCommentsResponseDto = GetCommentsResponseDto.builder()
                .commentId(commentId)
                .content(comment.get().getContent())
                .profileId(comment.get().getProfile().getProfileId())
                .profileDogName(comment.get().getProfile().getDogName())
                .profileImageUrl(comment.get().getProfile().getImageUrl())
                .postId(comment.get().getPost().getPostId())
                .likeCount(comment.get().getLikeCount())
                .createdAt(timeAgoUtil.getTimeAgo(comment.get().getRegdate()))
                .build();
        if (comment.get().getProfile().getProfileId().equals(profileId)) {
            getCommentsResponseDto.setOwner(true);
        }
        return ResponseEntity.ok(getCommentsResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<String> deleteComment(Long commentId, Long profileId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없습니다.");
        }
        if (comment.get().getProfile().getProfileId().equals(profileId)) {
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok().body("삭제 완료했습니다.");
        } else {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<String> updateComment(Long commentId, String content, Long profileId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없는 댓글입니다.");
        }
        if (comment.get().getProfile().getProfileId().equals(profileId)) {
            comment.get().setContent(content);
            return ResponseEntity.ok().body("수정 되었습니다.");
        }
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }
}
