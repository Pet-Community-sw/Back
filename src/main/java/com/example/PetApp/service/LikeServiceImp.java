package com.example.PetApp.service;

import com.example.PetApp.domain.LikeT;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.repository.LikeRepository;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImp implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional//나중에 profile에 관련된 정보를 보낼 수 있으니까
    @Override//LikeResponseDto를 반환하자.
    public ResponseEntity<Object> getLike(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없는 게시물입니다.");
        }else {
            Long likeCount = likeRepository.countByPostId(postId);
            LikeResponseDto likeResponseDto = new LikeResponseDto();
            likeResponseDto.setLikeCount(likeCount);
            return ResponseEntity.ok(likeResponseDto);
        }
    }

    @Transactional//일단 좋아요 갯수만 보여줄거여서 혹시 모르니까 likeid를 함께 반환
    public ResponseEntity<Object> createLike(Profile profile, Long postId) {
            LikeT likeT = LikeT.builder()
                    .postId(postId)
                    .profile(profile)
                    .build();
            likeRepository.save(likeT);
            return ResponseEntity.ok().body("좋아요 생성.");
    }

    @Transactional
    @Override
    public ResponseEntity<Object> createAndDeleteLike(LikeDto likeDto, String email) {
        Optional<Post> post = postRepository.findById(likeDto.getPostId());
        Optional<Profile> profile = profileRepository.findById(likeDto.getProfileId());
        Member member = memberRepository.findByEmail(email).get();

        if (post.isEmpty()||profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물 혹은 프로필이 유효하지 않습니다.");
        }

        boolean isOwner = likeRepository.existsByPostIdAndProfileProfileId(likeDto.getPostId(), likeDto.getProfileId());

        if (!profile.get().getMemberId().equals(member.getMemberId())) {
            return ResponseEntity.badRequest().body("member와 Profile이 일치하지 않습니다.");

        } else {
            if (isOwner) {
                return deleteLike(likeDto.getPostId(), likeDto.getProfileId());
            }else {
                return createLike(profile.get(), likeDto.getPostId());
            }
        }
    }

    @Transactional
    public ResponseEntity<Object> deleteLike(Long postId, Long profileId) {
        likeRepository.deleteByPostIdAndProfileProfileId(postId, profileId);
        return ResponseEntity.ok().body("삭제 완료.");
    }

}
