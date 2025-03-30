package com.example.PetApp.service;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
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
    public ResponseEntity<Object> createComment(CommentDto commentDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Profile> profile = profileRepository.findById(commentDto.getProfileId());
        Optional<Post> post = postRepository.findById(commentDto.getPostId());
        if (post.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 게시물은 없습니다.");
        }
        if (profile.isEmpty()||!(profile.get().getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        Comment comment=Comment.builder()
                .content(commentDto.getContent())
                .postId(commentDto.getPostId())
                .profile(profile.get())
                .build();
        Comment newComment = commentRepository.save(comment);
        return ResponseEntity.ok(newComment.getCommentId());
    }

    @Transactional
    @Override
    public ResponseEntity<Object> getComment(Long commentId, String email) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Member member = memberRepository.findByEmail(email).get();
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
                .postId(comment.get().getPostId())
                .likeCount(comment.get().getLikeCount())
                .createdAt(timeAgoUtil.getTimeAgo(comment.get().getRegdate()))
                .build();
        if (comment.get().getProfile().getMemberId().equals(member.getMemberId())) {
            getCommentsResponseDto.setOwner(true);
        }
        return ResponseEntity.ok(getCommentsResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<String> deleteComment(Long commentId, String email) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Member member = memberRepository.findByEmail(email).get();
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없습니다.");
        }
        if (comment.get().getProfile().getMemberId().equals(member.getMemberId())) {
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok().body("삭제 완료했습니다.");
        } else {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<String> updateComment(Long commentId, String content, String email) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Member member = memberRepository.findByEmail(email).get();
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없는 댓글입니다.");
        }
        if (comment.get().getProfile().getMemberId().equals(member.getMemberId())) {
            comment.get().setContent(content);
            return ResponseEntity.ok().body("수정 되었습니다.");
        }
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }
}
