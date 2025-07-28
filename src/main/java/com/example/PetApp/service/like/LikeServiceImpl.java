package com.example.PetApp.service.like;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.like.Like;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.LikeMapper;
import com.example.PetApp.repository.jpa.*;
import com.example.PetApp.util.SendNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor//like를 superclass로 둠으로써 likeId 겹칠일이없음.
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final LikeRepository likeRepository;
    private final SendNotificationUtil sendNotificationUtil;


    @Transactional(readOnly = true)
    @Override
    public LikeResponseDto getLikes(Long postId) {
        log.info("getLikes 요청 postId : {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
        return LikeMapper.toLikeResponseDto(post.getLikes());
    }

    public ResponseEntity<?> createAndDeleteLike(Long postId, String email) {
        log.info("createAndDeleteLike 요청 postId : {}", postId);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
        Optional<Like> existingLike = post.getLikes().stream()
                .filter(like -> like.getMember().equals(member))
                .findFirst();
        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            likeRepository.delete(like);
        } else {

        }


    }

    @Transactional
    @Override
    public ResponseEntity<?> createAndDeleteLike(Long postId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));

        Object post;
        switch (postId.getPostType()) {
            case COMMUNITY -> {
                post = postRepository.findById(postId.getPostId())
                        .orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
            }
            case RECOMMEND -> {
                post = recommendRoutePostRepository.findById(postId.getPostId())
                        .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
            }
            default -> {
                throw new IllegalArgumentException("잘못된 postType입니다.");
            }
        }

        // 좋아요 존재 여부
        boolean isLiked = existsLike(post, member);

        if (isLiked) {
            log.info("좋아요 삭제");
            deleteLike(post, member);
            return ResponseEntity.ok().body("좋아요 삭제했습니다.");
        } else {
            log.info("좋아요 생성");
            sendLikeNotification(post, member);
            createLike(post, member);
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
            sendNotificationUtil.sendNotification(postOwner, message);
    }
}
