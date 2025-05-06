package com.example.PetApp.service.like;

import com.example.PetApp.config.redis.NotificationRedisPublisher;
import com.example.PetApp.domain.LikeT;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImp implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationRedisPublisher notificationRedisPublisher;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    @Transactional
    @Override//member의 이름 과 사진으로
    //좋아요는 redis가 아니라 누르는순간 요청을 보내는게 맞고 새로고침할 때 마다 좋아요 리셋하는게 맞을듯.
    public ResponseEntity<Object> getLike(Long postId) {
        log.info("좋아요 상세 요청");
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없는 게시물입니다.");
        }else {
            List<LikeT> likeList = likeRepository.findAllByPost(post.get());
            LikeResponseDto likeResponseDto = new LikeResponseDto();
            likeList.forEach(likeT -> likeResponseDto.getMembers().add(likeT.getMember()));//수정해야됨.
            likeResponseDto.setLikeCount((long) likeResponseDto.getMembers().size());
            return ResponseEntity.ok(likeResponseDto);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Object> createAndDeleteLike(LikeDto likeDto, String email) {
        Optional<Post> post = postRepository.findById(likeDto.getPostId());
        Member member = memberRepository.findByEmail(email).get();
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        boolean isCheck = likeRepository.existsByPostAndMember(post.get(), member);
            if (isCheck) {
                log.info("좋아요 삭제");
                return deleteLike(post.get(),member);
            } else {
                log.info("좋아요 생성");
                String message = member.getName() + "님이 회원님의 게시물을 좋아합니다.";
                String key = "notifications:" + post.get().getMember().getMemberId() + ":" + UUID.randomUUID();//알림 설정 최대 3일.
                notificationRedisTemplate.opsForValue().set(key, message, Duration.ofDays(3));
                notificationRedisPublisher.publish("member:" + post.get().getMember().getMemberId(), message);
                return createLike(post.get(), member);
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

    @Transactional
    public ResponseEntity<Object> deleteLike(Post post, Member member) {

        likeRepository.deleteByPostAndMember(post, member);
        return ResponseEntity.ok().body("좋아요 삭제했습니다.");
    }

}
