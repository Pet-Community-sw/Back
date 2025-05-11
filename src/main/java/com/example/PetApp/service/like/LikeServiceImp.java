package com.example.PetApp.service.like;

import com.example.PetApp.config.redis.NotificationRedisPublisher;
import com.example.PetApp.domain.LikeT;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.dto.commment.LikeListDto;
import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.dto.notification.NotificationListDto;
import com.example.PetApp.firebase.FcmService;
import com.example.PetApp.repository.jpa.LikeRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImp implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationRedisPublisher notificationRedisPublisher;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final ObjectMapper objectMapper;
    private final FcmService fcmService;
    private final StringRedisTemplate stringRedisTemplate;

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
            List<LikeListDto> likeListDtos=likeList.stream()
                    .map(likeT ->LikeListDto.builder()
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
    }

    @Transactional
    @Override
    public ResponseEntity<Object> createAndDeleteLike(LikeDto likeDto, String email) throws JsonProcessingException {
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
                sendLikeNotification(post, member);

                return createLike(post.get(), member);
            }

    }

    private void sendLikeNotification(Optional<Post> post, Member member) throws JsonProcessingException {
        String message = member.getName() + "님이 회원님의 게시물을 좋아합니다.";
        String key = "notifications:" + post.get().getMember().getMemberId() + ":" + UUID.randomUUID();//알림 설정 최대 3일.
        NotificationListDto notificationListDto = new NotificationListDto(message, LocalDateTime.now());
        String json = objectMapper.writeValueAsString(notificationListDto);
        notificationRedisTemplate.opsForValue().set(key, json, Duration.ofDays(3));
        notificationRedisPublisher.publish("member:" + post.get().getMember().getMemberId(), message);
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember("foreGroundMembers:", member.getMemberId()))) {
            notificationRedisPublisher.publish("member:" + post.get().getMember().getMemberId(), message);
        }else {
            fcmService.sendNotification(post.get().getMember().getFcmToken().getFcmToken(), "명냥로드", message);
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
