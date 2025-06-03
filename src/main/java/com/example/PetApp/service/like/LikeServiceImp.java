package com.example.PetApp.service.like;

import com.example.PetApp.domain.LikeT;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.LikeMapper;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.service.comment.PostType;
import com.example.PetApp.util.SendNotificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImp implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final SendNotificationUtil sendNotificationUtil;

    @Transactional(readOnly = true)
    @Override
    public LikeResponseDto getLikes(PostType postType, Long postId) {
        log.info("getLikes 요청 postType : {}, postId : {}", postType, postId);
        List<LikeT> likes = new ArrayList<>();
        if (postType == PostType.COMMUNITY) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
            likes = likeRepository.findAllByPost(post);
        } else if (postType == PostType.RECOMMEND) {
            RecommendRoutePost recommendRoutePost = recommendRoutePostRepository.findById(postId)
                    .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
            likes = likeRepository.findAllByRecommendRoutePost(recommendRoutePost);
        }
        return LikeMapper.toLikeResponseDto(LikeMapper.toLikeListDto(likes));
    }

    @Transactional
    @Override
    public ResponseEntity<?> createAndDeleteLike(LikeDto likeDto, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));

        Object postEntity;
        if (likeDto.getPostType() == PostType.COMMUNITY) {
            postEntity = postRepository.findById(likeDto.getPostId())
                    .orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
        } else if (likeDto.getPostType() == PostType.RECOMMEND) {
            postEntity = recommendRoutePostRepository.findById(likeDto.getPostId())
                    .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
        } else {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        // 좋아요 존재 여부
        boolean isLiked = existsLike(postEntity, member);

        if (isLiked) {
            log.info("좋아요 삭제");
            deleteLike(postEntity, member);
            return ResponseEntity.ok().body("좋아요 삭제했습니다.");
        } else {
            log.info("좋아요 생성");
            sendLikeNotification(postEntity, member);
            createLike(postEntity, member);
            return ResponseEntity.status(HttpStatus.CREATED).body("좋아요 생성했습니다.");
        }
    }

    private <T> void createLike(T postEntity, Member member) {
        LikeT.LikeTBuilder builder = LikeT.builder().member(member);
        if (postEntity instanceof Post) {
            builder.post((Post) postEntity);
        } else if (postEntity instanceof RecommendRoutePost) {
            builder.recommendRoutePost((RecommendRoutePost) postEntity);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
        likeRepository.save(builder.build());
    }


    private <T> void deleteLike(T postEntity, Member member) {
        if (postEntity instanceof Post) {
            likeRepository.deleteByPostAndMember((Post) postEntity, member);
        } else if (postEntity instanceof RecommendRoutePost) {
            likeRepository.deleteByRecommendRoutePostAndMember((RecommendRoutePost) postEntity, member);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
    }

    private <T> boolean existsLike(T postEntity, Member member) {
        if (postEntity instanceof Post) {
            return likeRepository.existsByPostAndMember((Post) postEntity, member);
        } else if (postEntity instanceof RecommendRoutePost) {
            return likeRepository.existsByRecommendRoutePostAndMember((RecommendRoutePost) postEntity, member);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
    }

    private <T> void sendLikeNotification(T postEntity, Member liker) {
        Member postOwner;
        if (postEntity instanceof Post) {
            postOwner = ((Post) postEntity).getMember();
        } else if (postEntity instanceof RecommendRoutePost) {
            postOwner = ((RecommendRoutePost) postEntity).getMember();
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
        String message = liker.getName() + "님이 회원님의 게시물을 좋아합니다.";
        try {
            sendNotificationUtil.sendNotification(postOwner, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("알림 전송 중 에러", e);
        }
    }
}
