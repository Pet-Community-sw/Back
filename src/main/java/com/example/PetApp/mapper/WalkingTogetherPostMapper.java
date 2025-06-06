package com.example.PetApp.mapper;

import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.GetWalkingTogetherPostResponseDto;
import com.example.PetApp.util.TimeAgoUtil;

import java.util.List;
import java.util.stream.Collectors;

public class WalkingTogetherPostMapper {

    public static WalkingTogetherPost toEntity(Profile profile, RecommendRoutePost recommendRoutePost, CreateWalkingTogetherPostDto createWalkingTogetherPostDto) {
        return WalkingTogetherPost.builder()
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .scheduledTime(createWalkingTogetherPostDto.getScheduledTime())
                .limitCount(createWalkingTogetherPostDto.getLimitCount())
                .build();
    }


    public static List<GetWalkingTogetherPostResponseDto> toGetWalkingTogetherPostResponseDtos(List<WalkingTogetherPost> walkingTogetherPosts,
                                                                                               PetBreed petBreed) {
        return walkingTogetherPosts.stream()
                .map(walkingTogetherPost -> toGetWalkingTogetherPostResponseDto
                        (walkingTogetherPost.getWalkingTogetherPostId(),
                                walkingTogetherPost,
                                walkingTogetherPost.getProfile(),
                                petBreed
                        )).collect(Collectors.toList());
    }
    public static GetWalkingTogetherPostResponseDto toGetWalkingTogetherPostResponseDto(Long walkingTogetherPostId,
                                                                                        WalkingTogetherPost walkingTogetherPost,
                                                                                        Profile profile,
                                                                                        PetBreed petBreed) {
        return GetWalkingTogetherPostResponseDto.builder()
                .walkingTogetherPostId(walkingTogetherPostId)
                .petName(walkingTogetherPost.getProfile().getPetName())
                .petImageUrl(walkingTogetherPost.getProfile().getPetImageUrl())
                .scheduledTime(walkingTogetherPost.getScheduledTime())
                .currentCount(walkingTogetherPost.getProfiles().size())
                .limitCount(walkingTogetherPost.getLimitCount())
                .createdAt(TimeAgoUtil.getTimeAgo(walkingTogetherPost.getWalkingTogetherPostTime()))
                .isOwner(walkingTogetherPost.getProfile().equals(profile))
                .filtering(walkingTogetherPost.getAvoidBreeds().contains(petBreed.getPetBreedId()))//true이면 신청불가
                .build();

    }
}
