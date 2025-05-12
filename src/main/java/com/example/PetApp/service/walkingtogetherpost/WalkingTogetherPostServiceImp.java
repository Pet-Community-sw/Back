package com.example.PetApp.service.walkingtogetherpost;


import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.GetWalkingTogetherPostResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.UpdateWalkingTogetherPostDto;
import com.example.PetApp.repository.jpa.WalkingTogetherPostRepository;
import com.example.PetApp.repository.jpa.PetBreedRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
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
public class WalkingTogetherPostServiceImp implements WalkingTogetherPostService {

    private final ChatRoomService chatRoomService;
    private final WalkingTogetherPostRepository walkingTogetherPostRepository;
    private final ProfileRepository profileRepository;
    private final PetBreedRepository petBreedRepository;
    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final TimeAgoUtil timeAgoUtil;


    @Transactional
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public ResponseEntity<?> getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        log.info("getWalingTogetherPost 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId,profileId);
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요");
        }
        Optional<WalkingTogetherPost> walkingTogetherPost = walkingTogetherPostRepository.findById(walkingTogetherPostId);
        Profile profile = profileRepository.findById(profileId).get();
        Optional<PetBreed> petBreed = petBreedRepository.findByName(profile.getPetBreed());
        if (walkingTogetherPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 매칭글은 없습니다.");
        }
        GetWalkingTogetherPostResponseDto getWalkingTogetherPostResponseDto = getGetWalkingTogetherPostResponseDto(walkingTogetherPostId, walkingTogetherPost.get(), profile, petBreed.get());

        return ResponseEntity.ok(getWalkingTogetherPostResponseDto);
    }

    @Override
    public ResponseEntity<?> getWalkingTogetherPostsList(Long recommendRoutePostId, Long profileId) {
        log.info("getWalkingTogetherPostsList 요청 recommendRoutePostId : {}, profileId : {}", recommendRoutePostId, profileId);
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요");
        }
        Optional<RecommendRoutePost> recommendRoutePost = recommendRoutePostRepository.findById(recommendRoutePostId);
        if (recommendRoutePost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책길 추천 게시물은 없습니다.");
        }
        List<WalkingTogetherPost> walkingTogetherPosts = walkingTogetherPostRepository.findAllByRecommendRoutePost(recommendRoutePost.get());
        List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPostResponseDtoList = walkingTogetherPosts.stream()
                .map(walkingTogetherPost -> getGetWalkingTogetherPostResponseDto
                        (walkingTogetherPost.getWalkingTogetherPostId(),
                                walkingTogetherPost,
                                walkingTogetherPost.getProfile(),
                                petBreedRepository.findByName(walkingTogetherPost.getProfile().getPetBreed()).get())
                ).collect(Collectors.toList());
        return ResponseEntity.ok(getWalkingTogetherPostResponseDtoList);


    }

    @Transactional
    @Override
    public ResponseEntity<?> createWalkingTogetherPost(CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Long profileId) {
        log.info("createWalkingTogetherPost 요청 profileId : {}", profileId);
        Optional<RecommendRoutePost> recommendRoutePost = recommendRoutePostRepository.findById(createWalkingTogetherPostDto.getRecommendRoutePostId());
        if (recommendRoutePost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책길 추천 게시물은 없습니다.");
        }
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요");
        }
        Profile profile = profileRepository.findById(profileId).get();
        WalkingTogetherPost walkingTogetherPost = WalkingTogetherPost.builder()
                .profile(profile)
                .recommendRoutePost(recommendRoutePost.get())
                .scheduledTime(createWalkingTogetherPostDto.getScheduledTime())
                .limitCount(createWalkingTogetherPostDto.getLimitCount())
                .build();
        walkingTogetherPost.addMatchPostProfiles(profileId);
        walkingTogetherPost.addAvoidBreeds(profile);
        WalkingTogetherPost saveWalkingTogetherPost = walkingTogetherPostRepository.save(walkingTogetherPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("matchPostId", saveWalkingTogetherPost.getWalkingTogetherPostId()));
        //굳이 반환값을 id로 줘야하나? 낭비가 심한것같음.
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        log.info("deleteWalkingTogetherPost 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId, profileId);
        Optional<WalkingTogetherPost> walkingTogetherPost = walkingTogetherPostRepository.findById(walkingTogetherPostId);
        if (walkingTogetherPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 같이 산책해요 게시물은 없습니다");
        } else if (!walkingTogetherPost.get().getProfile().getProfileId().equals(profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청");
        }

        walkingTogetherPostRepository.deleteById(walkingTogetherPostId);
        return ResponseEntity.ok().body("삭제 완료");
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Long profileId) {
        Optional<WalkingTogetherPost> walkingTogetherPost = walkingTogetherPostRepository.findById(walkingTogetherPostId);
        if (walkingTogetherPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 같이 산책해요 게시물은 없습니다");
        } else if (!walkingTogetherPost.get().getProfile().getProfileId().equals(profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청");
        }
        walkingTogetherPost.get().setScheduledTime(updateWalkingTogetherPostDto.getScheduledTime());
        walkingTogetherPost.get().setLimitCount(updateWalkingTogetherPostDto.getLimitCount());

        return ResponseEntity.ok().body("수정 완료");
    }

    @Transactional
    @Override
    public ResponseEntity<?> startMatch(Long walkingTogetherPostId, Long profileId) {
        log.info("스타트 매칭");
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요.");
        }
        Optional<WalkingTogetherPost> walkingTogetherPost = walkingTogetherPostRepository.findById(walkingTogetherPostId);
        if (walkingTogetherPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매칭게시물은 없습니다");
        }
        if (walkingTogetherPost.get().getProfiles().contains(profileId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 채팅방에 들어가있습니다.");
        }
        Profile profile = profileRepository.findById(profileId).get();
        Optional<PetBreed> petBreed = petBreedRepository.findByName(profile.getPetBreed());
        if (walkingTogetherPost.get().getAvoidBreeds().contains(petBreed.get().getPetBreedId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 종은 참여할 수 없습니다.");
        }
        walkingTogetherPost.get().addMatchPostProfiles(profileId);
        walkingTogetherPost.get().addAvoidBreeds(profile);
        return chatRoomService.createChatRoom(walkingTogetherPost.get(), profile);
    }

    private GetWalkingTogetherPostResponseDto getGetWalkingTogetherPostResponseDto(Long walkingTogetherPostId, WalkingTogetherPost walkingTogetherPost, Profile profile, PetBreed petBreed) {
        return GetWalkingTogetherPostResponseDto.builder()
                .walkingTogetherPostId(walkingTogetherPostId)
                .petName(walkingTogetherPost.getProfile().getPetName())
                .petImageUrl(walkingTogetherPost.getProfile().getPetImageUrl())
                .scheduledTime(walkingTogetherPost.getScheduledTime())
                .currentCount(walkingTogetherPost.getProfiles().size())
                .limitCount(walkingTogetherPost.getLimitCount())
                .createdAt(timeAgoUtil.getTimeAgo(walkingTogetherPost.getWalkingTogetherPostTime()))
                .isOwner(walkingTogetherPost.getProfile().equals(profile))
                .filtering(walkingTogetherPost.getAvoidBreeds().contains(petBreed.getPetBreedId()))//true이면 신청불가
                .build();
    }
}
