package com.example.PetApp.service.comment;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import com.example.PetApp.repository.jpa.CommentRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.util.SendNotificationUtil;
import com.example.PetApp.util.TimeAgoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor//memberId를 받아서 유효성 검사해야할듯.
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final MemberRepository memberRepository;
    private final TimeAgoUtil timeAgoUtil;
    private final SendNotificationUtil sendNotificationUtil;

    @Transactional
    @Override//리펙토링 필수.
    public ResponseEntity<?> createComment(CommentDto commentDto, String email) throws JsonProcessingException {
        Member member = memberRepository.findByEmail(email).get();
        log.info("createComment 요청 memberId : {}", member.getMemberId());
        if (commentDto.getPostType() == CommentDto.PostType.COMMUNITY) {
            log.info("CommunityPost");
            Optional<Post> post = postRepository.findById(commentDto.getPostId());
            if (post.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
            }
            Comment comment = Comment.builder()
                    .content(commentDto.getContent())
                    .post(post.get())
                    .member(member)
                    .build();

            commentRepository.save(comment);
            String message = member.getName() + "님이 회원님의 게시물에 댓글을 남겼습니다.";
            sendNotificationUtil.sendNotification(post.get().getMember(), message); // 알림 전송
            return ResponseEntity.status(HttpStatus.CREATED).body(comment.getCommentId());
        } else if (commentDto.getPostType() == CommentDto.PostType.RECOMMEND) {
            log.info("RecommendPost");
            Optional<RecommendRoutePost> post = recommendRoutePostRepository.findById(commentDto.getPostId());
            if (post.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
            }
            Comment comment = Comment.builder()
                    .content(commentDto.getContent())
                    .recommendRoutePost(post.get())
                    .member(member)
                    .build();

            commentRepository.save(comment);
            String message = member.getName() + "님이 회원님의 게시물에 댓글을 남겼습니다.";
            sendNotificationUtil.sendNotification(post.get().getMember(), message); // 알림 전송
            return ResponseEntity.status(HttpStatus.CREATED).body(comment.getCommentId());
        } else {
            return ResponseEntity.badRequest().body("지원하지 않는 게시물 유형입니다.");
        }
    }


    @Transactional
    @Override
    public ResponseEntity<String> deleteComment(Long commentId, String email) {
        log.info("댓글 삭제 요청");
        Member member = memberRepository.findByEmail(email).get();
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없습니다.");
        }
        if (comment.get().getMember().equals(member)) {
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok().body("삭제 완료했습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청입니다.");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<String> updateComment(Long commentId, UpdateCommentDto updateCommentDto, String email) {
        log.info("댓글 수정 요청");
        Optional<Comment> comment = commentRepository.findById(commentId);
        Member member = memberRepository.findByEmail(email).get();
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없는 댓글입니다.");
        }
        if (comment.get().getMember().equals(member)) {
            comment.get().setContent(updateCommentDto.getContent());
            return ResponseEntity.ok().body("수정 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청입니다.");
        }
    }

    @Transactional
    @Override//likeCount수정해야함.
    public ResponseEntity<?> getComments(Long recommendRoutePostId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getComments 요청 recommendRoutePostId : {}, memberId : {}", recommendRoutePostId, member.getMemberId());

        Optional<RecommendRoutePost> post = recommendRoutePostRepository.findById(recommendRoutePostId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책길 추천 게시글은 없습니다.");
        }

        List<Comment> comments = commentRepository.findAllByRecommendRoutePost(post.get());
        List<GetCommentsResponseDto> getCommentsResponseDtos=comments.stream()
                .map(comment -> new GetCommentsResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getMember().getMemberId(),
                        comment.getMember().getName(),
                        comment.getMember().getMemberImageUrl(),
                        timeAgoUtil.getTimeAgo(comment.getCommentTime()),
                        comment.getMember().equals(member)
                )).collect(Collectors.toList());

        return ResponseEntity.ok(getCommentsResponseDtos);
    }

}
