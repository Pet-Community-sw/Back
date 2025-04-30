package com.example.PetApp.service.matchpost;


import com.example.PetApp.domain.MatchPost;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.matchpost.CreateMatchPostDto;
import com.example.PetApp.repository.jpa.MatchPostRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchPostServiceImp implements MatchPostService {

    private final MatchPostRepository matchPostRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public ResponseEntity<?> getMatchPosts(Long profileId) {
        log.info("매칭글 리스트 요청");
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요");
        }
        List<MatchPost> matchPosts = matchPostRepository.findAll();
        return ResponseEntity.ok(matchPosts);
    }

    @Transactional
    @Override
    public ResponseEntity<?> createMatchPost(CreateMatchPostDto createMatchPostDto, Long profileId) {
        log.info("매칭글 생성 요청");
        if (!createMatchPostDto.getProfileId().equals(profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청");
        }
        Profile profile = profileRepository.findById(profileId).get();
        MatchPost matchPost = MatchPost.builder()
                .profile(profile)
                .latitude(createMatchPostDto.getLatitude())
                .longitude(createMatchPostDto.getLongitude())
                .content(createMatchPostDto.getContent())
                .limitCount(createMatchPostDto.getLimitCount())
                .locationName(createMatchPostDto.getLocationName())
                .build();
        matchPost.addMatchPostProfiles(profileId);
        profile.getAvoidBreeds().forEach(avoidBreeds -> matchPost.addAvoidBreeds(avoidBreeds.getPetBreedId()));
        MatchPost saveMatchPost = matchPostRepository.save(matchPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("matchPostId", saveMatchPost.getMatchPostId()));
        //굳이 반환값을 id로 줘야하나? 낭비가 심한것같음.
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteMatchPost(Long matchPostId, Long profileId) {
        log.info("매칭게시물 삭제 요청");
        Optional<MatchPost> matchPost = matchPostRepository.findById(matchPostId);
        Profile profile = profileRepository.findById(profileId).get();
        if (matchPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매칭게시물은 없습니다.");
        } else if (!matchPost.get().getProfile().equals(profile)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청");
        }

        matchPostRepository.deleteById(matchPostId);
        return ResponseEntity.ok().body("삭제 완료.");
    }
}
