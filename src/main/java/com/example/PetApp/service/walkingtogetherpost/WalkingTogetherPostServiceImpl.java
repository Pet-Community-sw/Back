package com.example.PetApp.service.walkingtogetherpost;


import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherMatch;
import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostResponseDto;
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
import com.example.PetApp.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalkingTogetherPostServiceImpl implements WalkingTogetherPostService {

    private final ChatRoomService chatRoomService;
    private final WalkingTogetherPostRepository walkingTogetherPostRepository;
    private final ProfileRepository profileRepository;
    private final PetBreedRepository petBreedRepository;
    private final RecommendRoutePostRepository recommendRoutePostRepository;


    @Transactional(readOnly = true)
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public GetWalkingTogetherPostResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        log.info("getWalingTogetherPost 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId,profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 설정 해주세요."));
        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherPostRepository.findById(walkingTogetherPostId)
                .orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
        PetBreed petBreed = petBreedRepository.findByName(profile.getPetBreed().getName()).get();

        return WalkingTogetherPostMapper.toGetWalkingTogetherPostResponseDto(walkingTogetherPostId, walkingTogetherMatch, profile, petBreed);

    }

    @Transactional(readOnly = true)
    @Override
    public List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPosts(Long recommendRoutePostId, Long profileId) {
        log.info("getWalkingTogetherPostsList 요청 recommendRoutePostId : {}, profileId : {}", recommendRoutePostId, profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 설정 해주세요."));
        RecommendRoutePost recommendRoutePost = recommendRoutePostRepository.findById(recommendRoutePostId)
                .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
        PetBreed petBreed = petBreedRepository.findByName(profile.getPetBreed().getName()).get();
        List<WalkingTogetherMatch> walkingTogetherMatches = walkingTogetherPostRepository.findAllByRecommendRoutePost(recommendRoutePost);
        return WalkingTogetherPostMapper.toGetWalkingTogetherPostResponseDtos(walkingTogetherMatches, petBreed);
    }

    @Transactional
    @Override
    public CreateWalkingTogetherPostResponseDto createWalkingTogetherPost(CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Long profileId) {
        log.info("createWalkingTogetherPost 요청 profileId : {}", profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 설정 해주세요."));
        RecommendRoutePost recommendRoutePost = recommendRoutePostRepository.findById(createWalkingTogetherPostDto.getRecommendRoutePostId())
                .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
        WalkingTogetherMatch walkingTogetherMatch = WalkingTogetherPostMapper.toEntity(profile, recommendRoutePost, createWalkingTogetherPostDto);
        walkingTogetherMatch.addMatchPostProfiles(profileId);
        walkingTogetherMatch.addAvoidBreeds(profile);
        WalkingTogetherMatch savedWalkingTogetherMatch = walkingTogetherPostRepository.save(walkingTogetherMatch);
        return new CreateWalkingTogetherPostResponseDto(savedWalkingTogetherMatch.getWalkingTogetherPostId());
    }

    @Transactional
    @Override
    public void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Long profileId) {
        log.info("updateWalkingTogetherPost 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId, profileId);
        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherPostRepository.findById(walkingTogetherPostId)
                .orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
        if (!walkingTogetherMatch.getProfile().getProfileId().equals(profileId)) {
            throw new ForbiddenException("수정 권한 없음.");
        }
        walkingTogetherMatch.setScheduledTime(updateWalkingTogetherPostDto.getScheduledTime());
        walkingTogetherMatch.setLimitCount(updateWalkingTogetherPostDto.getLimitCount());
    }

    @Transactional
    @Override
    public void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        log.info("deleteWalkingTogetherPost 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId, profileId);
        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherPostRepository.findById(walkingTogetherPostId)
                .orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
        if (!walkingTogetherMatch.getProfile().getProfileId().equals(profileId)) {
            throw new ForbiddenException("삭제 권한 없음.");
        }
        walkingTogetherPostRepository.deleteById(walkingTogetherPostId);
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId) {
        log.info("startMatch 요청 walkingTogetherPostId : {}, profileId : {}", walkingTogetherPostId, profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 설정해주세요."));

        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherPostRepository.findById(walkingTogetherPostId)
                .orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));

        if (walkingTogetherMatch.getProfiles().contains(profileId)) {
            throw new ConflictException("이미 채팅방에 들어가있습니다.");
        }

        PetBreed petBreed = petBreedRepository.findByName(profile.getPetBreed().getName())
                .orElseThrow(() -> new NotFoundException("해당 견종을 찾을 수 없습니다."));

        if (walkingTogetherMatch.getAvoidBreeds().contains(petBreed.getPetBreedId())) {
            throw new ForbiddenException("해당 종은 참여할 수 없습니다.");
        }

        addMatchingAndAvoid(walkingTogetherMatch, profileId, profile);

        return chatRoomService.createChatRoom(walkingTogetherMatch, profile);
    }


    private void addMatchingAndAvoid(WalkingTogetherMatch post, Long profileId, Profile profile) {
        post.addMatchPostProfiles(profileId);
        post.addAvoidBreeds(profile);
    }
}
