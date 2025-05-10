package com.example.PetApp.service.comment;

import com.example.PetApp.config.redis.NotificationRedisPublisher;
import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import com.example.PetApp.dto.notification.NotificationListDto;
import com.example.PetApp.firebase.FcmService;
import com.example.PetApp.repository.jpa.CommentRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor//memberId를 받아서 유효성 검사해야할듯.
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationRedisPublisher notificationRedisPublisher;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final FcmService fcmService;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    @Override
    public ResponseEntity<Object> createComment(CommentDto commentDto, String email) throws JsonProcessingException {
        log.info("댓글 작성 요청.");
        Optional<Post> post = postRepository.findById(commentDto.getPostId());
        Member member = memberRepository.findByEmail(email).get();
        if (!(commentDto.getMemberId()).equals(member.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청입니다");
        }
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .post(post.get())
                .member(member)
                .build();
        Comment newComment = commentRepository.save(comment);
        sendCommentNotifications(post, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment.getCommentId());
    }

    private void sendCommentNotifications(Optional<Post> post, Member member) throws JsonProcessingException {
        String message = member.getName() + "님이 회원님의 게시물에 댓글을 남겼습니다.";
        String key = "notifications:" + post.get().getMember().getMemberId() + ":" + UUID.randomUUID();//알림 설정 최대 3일.
        NotificationListDto notificationListDto = new NotificationListDto(message, LocalDateTime.now());
        String json =objectMapper.writeValueAsString(notificationListDto);
        notificationRedisTemplate.opsForValue().set(key, json, Duration.ofDays(3));
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember("foreGroundMembers:", member.getMemberId()))) {
            notificationRedisPublisher.publish("member:" + post.get().getMember().getMemberId(), message);
        }else {
            fcmService.sendNotification(post.get().getMember().getFcmToken().getFcmToken(), "명냥로드", message);
        }
    }

//    @Transactional
//    @Override
//    public ResponseEntity<Object> getComment(Long commentId,String email) {
//        log.info("댓글 상세 요청");
//        Optional<Comment> comment = commentRepository.findById(commentId);
//        Member member = memberRepository.findByEmail(email).get();
//        if (comment.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글은 없는 댓글입니다.");
//        }
//        TimeAgoUtil timeAgoUtil = new TimeAgoUtil();
//        GetCommentsResponseDto getCommentsResponseDto = GetCommentsResponseDto.builder()
//                .commentId(commentId)
//                .content(comment.get().getContent())
//                .memberId(comment.get().getMember().getMemberId())
//                .memberName(comment.get().getMember().getName())
//                .likeCount(comment.get().getLikeCount())
//                .createdAt(timeAgoUtil.getTimeAgo(comment.get().getCommentTime()))
//                .build();
//        if (comment.get().getMember().equals(member)) {
//            getCommentsResponseDto.setOwner(true);
//        }
//        return ResponseEntity.ok(getCommentsResponseDto);
//    }

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
}
