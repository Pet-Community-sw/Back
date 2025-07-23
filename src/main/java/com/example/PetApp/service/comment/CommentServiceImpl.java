package com.example.PetApp.service.comment;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.commment.*;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.CommentMapper;
import com.example.PetApp.repository.jpa.CommentRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
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
    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final MemberRepository memberRepository;
    private final SendNotificationUtil sendNotificationUtil;

    @Transactional
    @Override
    public CreateCommentResponseDto createComment(CommentDto commentDto, String email) {
        log.info("createComment 요청 email : {}, commentType : {}", email, commentDto.getPostType());
        Member member = memberRepository.findByEmail(email).get();
        CommentAndMemberDto commentAndPostOwner = getCommentAndPostOwner(commentDto, member);

        Comment comment = commentAndPostOwner.getComment();
        commentRepository.save(comment);
        sendCommentNotification(commentAndPostOwner.getMember(), member);
        return new CreateCommentResponseDto(comment.getCommentId());
    }


    @Transactional
    @Override
    public void deleteComment(Long commentId, String email) {
        log.info("deleteComment 요청 email : {}, commentId : {}", email, commentId);
        Member member = memberRepository.findByEmail(email).get();
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
        Member member = memberRepository.findByEmail(email).get();

        if (!(comment.getMember().equals(member))) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        } else {
            comment.setContent(updateCommentDto.getContent());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetCommentsResponseDto> getComments(Long recommendRoutePostId, String email) {
        log.info("getComments 요청 email : {}, recommendRoutePostId : {}", email, recommendRoutePostId);
        Member member = memberRepository.findByEmail(email).get();

        RecommendRoutePost post = recommendRoutePostRepository.findById(recommendRoutePostId)
                .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
        return CommentMapper.toGetCommentsResponseDtos(post, member);
    }

    private void sendCommentNotification(Member postmember, Member member) {
        String message = member.getName() + "님이 회원님의 게시물에 댓글을 남겼습니다.";
        sendNotificationUtil.sendNotification(postmember, message); // 알림 전송
    }

    private CommentAndMemberDto getCommentAndPostOwner(CommentDto commentDto, Member member){
        Comment comment;
        Member postOwner;
        if (commentDto.getPostType() == PostType.COMMUNITY) {
            Post post = postRepository.findById(commentDto.getPostId())
                    .orElseThrow(() -> new NotFoundException("해당 자유게시물은 없습니다."));
            comment = CommentMapper.toEntity(commentDto, post, member);
            postOwner = post.getMember();
        } else if (commentDto.getPostType() ==PostType.RECOMMEND) {
            RecommendRoutePost recommendRoutePost = recommendRoutePostRepository.findById(commentDto.getPostId())
                    .orElseThrow(()->new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
            comment = CommentMapper.toEntity(commentDto, recommendRoutePost, member);
            postOwner = recommendRoutePost.getMember();
        } else {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        return new CommentAndMemberDto(comment, postOwner);
    }

}
