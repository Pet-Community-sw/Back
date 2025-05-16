package com.example.PetApp.service.like;

import com.example.PetApp.domain.LikeT;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.LikeListDto;
import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.util.SendNotificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor//테스트 해야됨.
public class LikeServiceImp implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final SendNotificationUtil sendNotificationUtil;

    @Transactional
    @Override//member의 이름 과 사진으로
    //좋아요는 redis가 아니라 누르는순간 요청을 보내는게 맞고 새로고침할 때 마다 좋아요 리셋하는게 맞을듯.
    public ResponseEntity<?> getLike(Long postId) {
        log.info("좋아요 상세 요청");
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없는 게시물입니다.");
        }
            List<LikeT> likeList = likeRepository.findAllByPost(post.get());
            return getLikeList(likeList);

    }

    @Transactional
    @Override
    public ResponseEntity<?> getLikeByRecommendRoutePost(Long recommendRoutePostId) {
        log.info("getLikeByRecommendRoutePostId 요청 recommendRoutePostId : {}", recommendRoutePostId);
        Optional<RecommendRoutePost> recommendRoutePost = recommendRoutePostRepository.findById(recommendRoutePostId);
        if (recommendRoutePost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책길 추전 게시글은 없습니다.");
        }
        List<LikeT> likeList = likeRepository.findAllByRecommendRoutePost(recommendRoutePost.get());
        return getLikeList(likeList);
    }

    @NotNull
    private ResponseEntity<?> getLikeList(List<LikeT> likeList) {
        List<LikeListDto> likeListDtos = likeList.stream()
                .map(likeT -> LikeListDto.builder()
                        .memberName(likeT.getMember().getName())
                        .memberImageUrl(likeT.getMember().getMemberImageUrl())
                        .build()
                ).collect(Collectors.toList());
        LikeResponseDto likeResponseDto = LikeResponseDto.builder()
                .likeListDtos(likeListDtos)
                .likeCount((long) likeList.size())
                .build();
        return ResponseEntity.ok(likeResponseDto);
    }

    @Transactional
    @Override//리펙토링 필수.
    public ResponseEntity<Object> createAndDeleteLike(LikeDto likeDto, String email) throws JsonProcessingException {
        Member member = memberRepository.findByEmail(email).get();
        if (likeDto.getPostType()== CommentDto.PostType.COMMUNITY) {
            Optional<Post> post = postRepository.findById(likeDto.getPostId());
            if (post.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
            }
            boolean isCheck = likeRepository.existsByPostAndMember(post.get(), member);

            if (isCheck) {
                log.info("좋아요 삭제");
                return deleteLike(post.get(), member);
            } else {
                log.info("좋아요 생성");
                String message = member.getName() + "님이 회원님의 게시물을 좋아합니다.";
                sendNotificationUtil.sendNotification(post.get().getMember(), message);

                return createLike(post.get(), member);
            }
        } else if (likeDto.getPostType() == CommentDto.PostType.RECOMMEND) {
            Optional<RecommendRoutePost> post = recommendRoutePostRepository.findById(likeDto.getPostId());
            if (post.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
            }
            boolean isCheck = likeRepository.existsByRecommendRoutePostAndMember(post.get(), member);

            if (isCheck) {
                log.info("좋아요 삭제");
                return deleteLike(post.get(), member);
            } else {
                log.info("좋아요 생성");
                String message = member.getName() + "님이 회원님의 게시물을 좋아합니다.";
                sendNotificationUtil.sendNotification(post.get().getMember(), message);

                return createLike(post.get(), member);
            }
        }else {
            return ResponseEntity.badRequest().body("지원하지 않는 게시물 유형입니다.");
        }

    }

    @Transactional//일단 좋아요 갯수만 보여줄거여서 혹시 모르니까 likeid를 함께 반환
    public ResponseEntity<Object> createLike(Post post, Member member) {
        LikeT likeT = LikeT.builder()
                .post(post)
                .member(member)
                .build();
        likeRepository.save(likeT);
        return ResponseEntity.status(HttpStatus.CREATED).body("좋아요 생성했습니다.");
    }

    @Transactional//일단 좋아요 갯수만 보여줄거여서 혹시 모르니까 likeid를 함께 반환
    public ResponseEntity<Object> createLike(RecommendRoutePost recommendRoutePost, Member member) {
        LikeT likeT = LikeT.builder()
                .recommendRoutePost(recommendRoutePost)
                .member(member)
                .build();
        likeRepository.save(likeT);
        return ResponseEntity.status(HttpStatus.CREATED).body("좋아요 생성했습니다.");
    }

    @Transactional
    public ResponseEntity<Object> deleteLike(Post post, Member member) {

        likeRepository.deleteByPostAndMember(post, member);
        return ResponseEntity.ok().body("좋아요 삭제했습니다.");
    }

    @Transactional
    public ResponseEntity<Object> deleteLike(RecommendRoutePost recommendRoutePost, Member member) {

        likeRepository.deleteByRecommendRoutePostAndMember(recommendRoutePost, member);
        return ResponseEntity.ok().body("좋아요 삭제했습니다.");
    }

}
