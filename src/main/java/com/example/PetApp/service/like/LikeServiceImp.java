package com.example.PetApp.service.like;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImp implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional//나중에 profile에 관련된 정보를 보낼 수 있으니까
    @Override//LikeResponseDto를 반환하자.
    //좋아요는 redis가 아니라 누르는순간 요청을 보내는게 맞고 새로고침할 때 마다 좋아요 리셋하는게 맞을듯.
    public ResponseEntity<Object> getLike(Long postId) {
        log.info("좋아요 상세 요청");
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없는 게시물입니다.");
        }else {
            Long likeCount = likeRepository.countByPost(post.get());
            LikeResponseDto likeResponseDto = new LikeResponseDto();
            likeResponseDto.setLikeCount(likeCount);
            return ResponseEntity.ok(likeResponseDto);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Object> createAndDeleteLike(LikeDto likeDto, String email) {
        Optional<Post> post = postRepository.findById(likeDto.getPostId());
        Member member = memberRepository.findByEmail(email).get();
        if (!(likeDto.getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청입니다.");
        }
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        boolean isCheck = likeRepository.existsByPostAndMember(post.get(), member);
            if (isCheck) {
                log.info("좋아요 생성");
                return deleteLike(post.get(),member);
            } else {
                log.info("좋아요 삭제");
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
