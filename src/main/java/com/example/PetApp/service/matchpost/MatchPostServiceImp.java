package com.example.PetApp.service.matchpost;


import com.example.PetApp.domain.MatchPost;
import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.matchpost.CreateMatchPostDto;
import com.example.PetApp.dto.matchpost.GetMatchPostListResponseDto;
import com.example.PetApp.dto.matchpost.GetMatchPostResponseDto;
import com.example.PetApp.dto.matchpost.UpdateMatchPostDto;
import com.example.PetApp.repository.jpa.MatchPostRepository;
import com.example.PetApp.repository.jpa.PetBreedRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.service.chat.ChatRoomService;
import com.example.PetApp.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchPostServiceImp implements MatchPostService {

    private final ChatRoomService chatRoomService;
    private final MatchPostRepository matchPostRepository;
    private final ProfileRepository profileRepository;
    private final PetBreedRepository petBreedRepository;
    private final TimeAgoUtil timeAgoUtil;
    @Transactional//반경 1km로 같이 불러옴 테스트해봐야됨.
    @Override//paging처리 해야할듯.
    public ResponseEntity<?> getMatchPostsByPlace(Double longitude, Double latitude, Long profileId) {
        log.info("getMatchPostsByPlace 요청");
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요");
        }
        List<MatchPost> matchPosts = matchPostRepository.findByMatchPostsByPlace(longitude, latitude);
        List<GetMatchPostListResponseDto> getMatchPostListResponseDtoList = matchPosts.stream()
                .map(matchPost -> new GetMatchPostListResponseDto(
                        matchPost.getMatchPostId(),
                        matchPost.getLocationName(),
                        matchPost.getLimitCount(),
                        timeAgoUtil.getTimeAgo(matchPost.getMatchPostTime()),
                        matchPost.getProfiles().size(),
                        matchPost.getLongitude(),
                        matchPost.getLatitude()
                )).collect(Collectors.toList());
        return ResponseEntity.ok(getMatchPostListResponseDtoList);
    }

    @Transactional//화면의 A좌표 B좌표를 받아서 그 안에 있는 게시물들을 반환.
    @Override//근데 아이콘의 크기때문에 어느정도의 공간을 둘 필요가 있음.
    public ResponseEntity<?> getMatchPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, Long profileId) {
        log.info("getMatchPostsByLocation 요청");
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요");
        }
        List<MatchPost> matchPosts = matchPostRepository.findByMatchPostByLocation(minLongitude-0.001, minLatitude-0.001, maxLongitude+0.001, maxLatitude+0.001);
        List<GetMatchPostListResponseDto> getMatchPostListResponseDtoList = matchPosts.stream()
                .map(matchPost -> new GetMatchPostListResponseDto(
                        matchPost.getMatchPostId(),
                        matchPost.getLocationName(),
                        matchPost.getLimitCount(),
                        timeAgoUtil.getTimeAgo(matchPost.getMatchPostTime()),
                        matchPost.getProfiles().size(),
                        matchPost.getLongitude(),
                        matchPost.getLatitude()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(getMatchPostListResponseDtoList);
    }


    @Transactional
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public ResponseEntity<?> getMatchPost(Long matchPostId, Long profileId) {
        log.info("상세매칭글 보기 요청 : {}", matchPostId);
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요");
        }
        Optional<MatchPost> matchPost = matchPostRepository.findById(matchPostId);
        Profile profile = profileRepository.findById(profileId).get();
        Optional<PetBreed> petBreed = petBreedRepository.findByName(profile.getPetBreed());
        if (matchPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 매칭글은 없습니다.");
        }
        GetMatchPostResponseDto getMatchPostResponseDto=GetMatchPostResponseDto.builder()
                .matchPostId(matchPostId)
                .petName(matchPost.get().getProfile().getPetName())
                .petImageUrl(matchPost.get().getProfile().getPetImageUrl())
                .content(matchPost.get().getContent())
                .locationName(matchPost.get().getLocationName())
                .currentCount(matchPost.get().getProfiles().size())
                .limitCount(matchPost.get().getLimitCount())
                .createdAt(timeAgoUtil.getTimeAgo(matchPost.get().getMatchPostTime()))
                .build();
        if (matchPost.get().getProfile().getProfileId().equals(profileId)) {
            getMatchPostResponseDto.setOwner(true);//작성자인지 확인
        }
        if (matchPost.get().getAvoidBreeds().contains(petBreed.get().getPetBreedId())) {
            getMatchPostResponseDto.setFiltering(true);//필터링
        }
        return ResponseEntity.ok(getMatchPostResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> createMatchPost(CreateMatchPostDto createMatchPostDto, Long profileId) {
        log.info("매칭글 생성 요청");
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
        matchPost.addAvoidBreeds(profile);
        MatchPost saveMatchPost = matchPostRepository.save(matchPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("matchPostId", saveMatchPost.getMatchPostId()));
        //굳이 반환값을 id로 줘야하나? 낭비가 심한것같음.
    }

    @Transactional
    @Override
    public ResponseEntity<?> startMatch(Long matchPostId, Long profileId) {
        log.info("스타트 매칭");
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요.");
        }
        Optional<MatchPost> matchPost = matchPostRepository.findById(matchPostId);
        if (matchPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매칭게시물은 없습니다");
        }
        if (matchPost.get().getProfiles().contains(profileId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 채팅방에 들어가있습니다.");
        }
        Profile profile = profileRepository.findById(profileId).get();
        Optional<PetBreed> petBreed = petBreedRepository.findByName(profile.getPetBreed());
        if (matchPost.get().getAvoidBreeds().contains(petBreed.get().getPetBreedId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 종은 참여할 수 없습니다.");
        }
        matchPost.get().addMatchPostProfiles(profileId);
        matchPost.get().addAvoidBreeds(profile);
        return chatRoomService.createChatRoom(matchPost.get(), profile);
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteMatchPost(Long matchPostId, Long profileId) {
        log.info("매칭게시물 삭제 요청");
        Optional<MatchPost> matchPost = matchPostRepository.findById(matchPostId);
        if (matchPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매칭게시물은 없습니다");
        } else if (!matchPost.get().getProfile().getProfileId().equals(profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청");
        }

        matchPostRepository.deleteById(matchPostId);
        return ResponseEntity.ok().body("삭제 완료");
    }

    @Transactional// 장소를 수정하는게 맞으려나?
    @Override
    public ResponseEntity<?> updateMatchPost(Long matchPostId, UpdateMatchPostDto updateMatchPostDto, Long profileId) {
        Optional<MatchPost> matchPost = matchPostRepository.findById(matchPostId);
        if (matchPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매칭게시물은 없습니다");
        } else if (!matchPost.get().getProfile().getProfileId().equals(profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청");
        }
        matchPost.get().setContent(updateMatchPostDto.getContent());
        matchPost.get().setLimitCount(updateMatchPostDto.getLimitCount());

        return ResponseEntity.ok().body("수정 완료");
    }
}
