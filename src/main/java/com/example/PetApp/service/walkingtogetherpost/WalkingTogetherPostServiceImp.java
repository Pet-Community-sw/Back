package com.example.PetApp.service.walkingtogetherpost;


import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.GetWalkingTogetherPostResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.UpdateWalkingTogetherPostDto;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.WalkingTogetherPostMapper;
import com.example.PetApp.repository.jpa.WalkingTogetherPostRepository;
import com.example.PetApp.repository.jpa.PetBreedRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.service.chat.ChatRoomService;
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


    @Transactional(readOnly = true)
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public ResponseEntity<?> getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        log.info("getWalingTogetherPost 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId,profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 설정 해주세요."));
        WalkingTogetherPost walkingTogetherPost = walkingTogetherPostRepository.findById(walkingTogetherPostId)
                .orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
        PetBreed petBreed = petBreedRepository.findByName(profile.getPetBreed()).get();

        GetWalkingTogetherPostResponseDto getWalkingTogetherPostResponseDto =
                WalkingTogetherPostMapper.toGetWalkingTogetherPostResponseDto(walkingTogetherPostId, walkingTogetherPost, profile, petBreed);

        return ResponseEntity.ok(getWalkingTogetherPostResponseDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPosts(Long recommendRoutePostId, Long profileId) {
        log.info("getWalkingTogetherPostsList 요청 recommendRoutePostId : {}, profileId : {}", recommendRoutePostId, profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 설정 해주세요."));
        RecommendRoutePost recommendRoutePost = recommendRoutePostRepository.findById(recommendRoutePostId)
                .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
        PetBreed petBreed = petBreedRepository.findByName(profile.getPetBreed()).get();
        List<WalkingTogetherPost> walkingTogetherPosts = walkingTogetherPostRepository.findAllByRecommendRoutePost(recommendRoutePost);
        return WalkingTogetherPostMapper.toGetWalkingTogetherPostResponseDtos(walkingTogetherPosts, petBreed);
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("프로필 설정 해주세요.");
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
    }

    @Transactional
    @Override
    public void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Long profileId) {
        Optional<WalkingTogetherPost> walkingTogetherPost = walkingTogetherPostRepository.findById(walkingTogetherPostId);
        if (walkingTogetherPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 함께 산책해요 게시글은 없습니다.");
        } else if (!walkingTogetherPost.get().getProfile().getProfileId().equals(profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한 없음.");
        }
        walkingTogetherPost.get().setScheduledTime(updateWalkingTogetherPostDto.getScheduledTime());
        walkingTogetherPost.get().setLimitCount(updateWalkingTogetherPostDto.getLimitCount());

        return ResponseEntity.ok().body("수정 완료.");
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        log.info("deleteWalkingTogetherPost 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId, profileId);
        Optional<WalkingTogetherPost> walkingTogetherPost = walkingTogetherPostRepository.findById(walkingTogetherPostId);
        if (walkingTogetherPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 함께 산책해요 게시글은 없습니다.");
        } else if (!walkingTogetherPost.get().getProfile().getProfileId().equals(profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한 없음.");
        }

        walkingTogetherPostRepository.deleteById(walkingTogetherPostId);
        return ResponseEntity.ok().body("삭제 완료.");
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId) {
        log.info("startMatch 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId, profileId);

        Profile profile = getProfileOrThrow(profileId);
        WalkingTogetherPost walkingTogetherPost = getWalkingTogetherPostOrThrow(walkingTogetherPostId);
        validateAlreadyMatched(walkingTogetherPost, profileId);
        PetBreed petBreed = getPetBreedOrThrow(profile.getPetBreed());
        validatePetBreedAllowed(walkingTogetherPost, petBreed);

        addMatchingAndAvoid(walkingTogetherPost, profileId, profile);

        return chatRoomService.createChatRoom(walkingTogetherPost, profile);
    }

    private Profile getProfileOrThrow(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 설정해주세요."));
    }

    private WalkingTogetherPost getWalkingTogetherPostOrThrow(Long postId) {
        return walkingTogetherPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }

    private void validateAlreadyMatched(WalkingTogetherPost post, Long profileId) {
        if (post.getProfiles().contains(profileId)) {
            throw new ConflictException("이미 채팅방에 들어가있습니다.");
        }
    }

    private PetBreed getPetBreedOrThrow(String breedName) {
        return petBreedRepository.findByName(breedName)
                .orElseThrow(() -> new NotFoundException("해당 견종을 찾을 수 없습니다."));
    }

    private void validatePetBreedAllowed(WalkingTogetherPost post, PetBreed petBreed) {
        if (post.getAvoidBreeds().contains(petBreed.getPetBreedId())) {
            throw new ForbiddenException("해당 종은 참여할 수 없습니다.");
        }
    }

    private void addMatchingAndAvoid(WalkingTogetherPost post, Long profileId, Profile profile) {
        post.addMatchPostProfiles(profileId);
        post.addAvoidBreeds(profile);
    }
}
