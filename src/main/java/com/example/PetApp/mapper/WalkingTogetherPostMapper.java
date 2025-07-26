package com.example.PetApp.mapper;

import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherMatch;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.GetWalkingTogetherPostResponseDto;
import com.example.PetApp.util.TimeAgoUtil;

import java.util.List;
import java.util.stream.Collectors;

public class WalkingTogetherPostMapper {

    public static WalkingTogetherMatch toEntity(Profile profile, RecommendRoutePost recommendRoutePost, CreateWalkingTogetherPostDto createWalkingTogetherPostDto) {
        return WalkingTogetherMatch.builder()
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .scheduledTime(createWalkingTogetherPostDto.getScheduledTime())
                .limitCount(createWalkingTogetherPostDto.getLimitCount())
                .build();
    }


    public static List<GetWalkingTogetherPostResponseDto> toGetWalkingTogetherPostResponseDtos(List<WalkingTogetherMatch> walkingTogetherMatches,
                                                                                               PetBreed petBreed) {
        return walkingTogetherMatches.stream()
                .map(walkingTogetherPost -> toGetWalkingTogetherPostResponseDto
                        (walkingTogetherPost.getWalkingTogetherPostId(),
                                walkingTogetherPost,
                                walkingTogetherPost.getProfile(),
                                petBreed
                        )).collect(Collectors.toList());
    }
    public static GetWalkingTogetherPostResponseDto toGetWalkingTogetherPostResponseDto(Long walkingTogetherPostId,
                                                                                        WalkingTogetherMatch walkingTogetherMatch,
                                                                                        Profile profile,
                                                                                        PetBreed petBreed) {
        return GetWalkingTogetherPostResponseDto.builder()
                .walkingTogetherPostId(walkingTogetherPostId)
                .petName(walkingTogetherMatch.getProfile().getPetName())
                .petImageUrl(walkingTogetherMatch.getProfile().getPetImageUrl())
                .scheduledTime(walkingTogetherMatch.getScheduledTime())
                .currentCount(walkingTogetherMatch.getProfiles().size())
                .limitCount(walkingTogetherMatch.getLimitCount())
                .createdAt(TimeAgoUtil.getTimeAgo(walkingTogetherMatch.getCreatedAt()))
                .isOwner(walkingTogetherMatch.getProfile().equals(profile))
                .filtering(walkingTogetherMatch.getAvoidBreeds().contains(petBreed.getPetBreedId()))//true이면 신청불가
                .build();

    }
}
