package com.example.PetApp.service.like;

import com.example.PetApp.domain.LikeT;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.PostRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
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
    private final ProfileRepository profileRepository;

    @Transactional//나중에 profile에 관련된 정보를 보낼 수 있으니까
    @Override//LikeResponseDto를 반환하자.
    public ResponseEntity<Object> getLike(Long postId) {
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
    public ResponseEntity<Object> createAndDeleteLike(LikeDto likeDto, Long profileId) {
        Optional<Post> post = postRepository.findById(likeDto.getPostId());
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        } else if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시물은 없습니다.");
        }
        boolean isOwner = likeRepository.existsByPostAndProfile(post.get(), profile.get());
        if (!profile.get().getProfileId().equals(profileId)) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");

        } else{
            if (isOwner) {
                return deleteLike(post.get(), profile.get());
            } else {
                return createLike(post.get(), profile.get());
            }
        }

    }

    @Transactional//일단 좋아요 갯수만 보여줄거여서 혹시 모르니까 likeid를 함께 반환
    public ResponseEntity<Object> createLike(Post post, Profile profile) {
        LikeT likeT = LikeT.builder()
                .post(post)
                .profile(profile)
                .build();
        likeRepository.save(likeT);
        return ResponseEntity.status(HttpStatus.CREATED).body("좋아요 생성했습니다.");
    }

    @Transactional
    public ResponseEntity<Object> deleteLike(Post post, Profile profile) {

        likeRepository.deleteByPostAndProfile(post, profile);
        return ResponseEntity.ok().body("좋아요 삭제했습니다.");
    }

}
