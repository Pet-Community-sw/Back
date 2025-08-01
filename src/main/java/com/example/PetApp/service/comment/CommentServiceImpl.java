package com.example.PetApp.service.comment;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.post.Commentable;
import com.example.PetApp.dto.commment.*;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.CommentMapper;
import com.example.PetApp.repository.jpa.CommentRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.util.SendNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor//memberId를 받아서 유효성 검사해야할듯.
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final SendNotificationUtil sendNotificationUtil;

    @Transactional(readOnly = true)
    @Override
    public List<GetCommentsResponseDto> getComments(Long postId, String email) {
        log.info("getComments 요청 email : {}, postId : {}", email, postId);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 회원은 없습니다."));

        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("해당 게시글은 없습니다."));
        return CommentMapper.toGetCommentsResponseDtos((Commentable) post, member);
    }

    @Transactional
    @Override
    public CreateCommentResponseDto createComment(CommentDto commentDto, String email) {
        log.info("createComment 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 회원은 없습니다."));
        Post post = postRepository.findById(commentDto.getPostId()).orElseThrow(() -> new NotFoundException("해당 게시글을 찾을 수 없습니다."));

        Comment comment = CommentMapper.toEntity(commentDto, post, member);
        commentRepository.save(comment);
        sendCommentNotification(post.getMember(), member);
        return new CreateCommentResponseDto(comment.getCommentId());
    }


    @Transactional
    @Override
    public void deleteComment(Long commentId, String email) {
        log.info("deleteComment 요청 email : {}, commentId : {}", email, commentId);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 회원은 없습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당 댓글은 없습니다."));
        if (!(comment.getMember().equals(member))) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public void updateComment(Long commentId, UpdateCommentDto updateCommentDto, String email) {
        log.info("updateComment 요청 email : {}, commentId : {}", email, commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당 댓글은 없습니다."));
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 회원은 없습니다."));

        if (!(comment.getMember().equals(member))) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        } else {
            comment.setContent(updateCommentDto.getContent());
        }
    }

    private void sendCommentNotification(Member postmember, Member member) {
        String message = member.getName() + "님이 회원님의 게시물에 댓글을 남겼습니다.";
        sendNotificationUtil.sendNotification(postmember, message); // 알림 전송
    }

}
