package com.example.PetApp.mapper;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.embedded.Location;
import com.example.PetApp.domain.embedded.PostContent;
import com.example.PetApp.dto.delegateWalkpost.CreateDelegateWalkPostDto;
import com.example.PetApp.dto.delegateWalkpost.GetDelegateWalkPostsResponseDto;
import com.example.PetApp.dto.delegateWalkpost.GetPostResponseDto;
import com.example.PetApp.dto.delegateWalkpost.UpdateDelegateWalkPostDto;
import com.example.PetApp.util.TimeAgoUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DelegateWalkPostMapper {

    public static DelegateWalkPost toEntity(CreateDelegateWalkPostDto createDelegateWalkPostDto, Profile profile) {
        return DelegateWalkPost.builder()
                .postContent(new PostContent(createDelegateWalkPostDto.getTitle(), createDelegateWalkPostDto.getContent()))
                .price(createDelegateWalkPostDto.getPrice())
                .location(new Location(createDelegateWalkPostDto.getLocationLongitude(), createDelegateWalkPostDto.getLocationLatitude()))
                .allowedRadiusMeters(createDelegateWalkPostDto.getAllowedRadiusMeters())
                .scheduledTime(createDelegateWalkPostDto.getScheduledTime())
                .profile(profile)
                .requireProfile(createDelegateWalkPostDto.isRequireProfile())
                .build();
    }

    public static void updateDelegateWalkPost(UpdateDelegateWalkPostDto updateDelegateWalkPostDto, DelegateWalkPost delegateWalkPost) {
        delegateWalkPost.setPostContent(new PostContent(updateDelegateWalkPostDto.getTitle(), updateDelegateWalkPostDto.getContent()));
        delegateWalkPost.setPrice(updateDelegateWalkPostDto.getPrice());
        delegateWalkPost.setAllowedRadiusMeters(updateDelegateWalkPostDto.getAllowedRedisMeters());
        delegateWalkPost.setRequireProfile(updateDelegateWalkPostDto.isRequireProfile());
        delegateWalkPost.setScheduledTime(updateDelegateWalkPostDto.getScheduledTime());
    }

    public static List<GetDelegateWalkPostsResponseDto> toGetDelegateWalkPostsResponseDtos(Member member, List<DelegateWalkPost> delegateWalkPosts) {
        return delegateWalkPosts.stream()
                .map(delegateWalkPost -> GetDelegateWalkPostsResponseDto.builder()
                        .delegateWalkPostId(delegateWalkPost.getDelegateWalkPostId())
                        .profileId(delegateWalkPost.getProfile().getProfileId())
                        .petName(delegateWalkPost.getProfile().getPetName())
                        .petImageUrl(delegateWalkPost.getProfile().getPetImageUrl())
                        .title(delegateWalkPost.getPostContent().getTitle())
                        .price(delegateWalkPost.getPrice())
                        .locationLongitude(delegateWalkPost.getLocation().getLocationLongitude())
                        .locationLatitude(delegateWalkPost.getLocation().getLocationLatitude())
                        .scheduledTime(delegateWalkPost.getScheduledTime())
                        .filtering(filter(delegateWalkPost, member))
                        .applicantCount(delegateWalkPost.getApplicants().size())
                        .createdAt(TimeAgoUtil.getTimeAgo(delegateWalkPost.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
    }

    public static GetPostResponseDto toGetPostResponseDto(DelegateWalkPost delegateWalkPost) {
        return GetPostResponseDto.builder()
                .delegateWalkPostId(delegateWalkPost.getDelegateWalkPostId())
                .title(delegateWalkPost.getPostContent().getTitle())
                .content(delegateWalkPost.getPostContent().getContent())
                .price(delegateWalkPost.getPrice())
                .locationLongitude(delegateWalkPost.getLocation().getLocationLongitude())
                .locationLatitude(delegateWalkPost.getLocation().getLocationLatitude())
                .allowedRadiusMeters(delegateWalkPost.getAllowedRadiusMeters())
                .scheduledTime(delegateWalkPost.getScheduledTime())
                .petName(delegateWalkPost.getProfile().getPetName())
                .petImageUrl(delegateWalkPost.getProfile().getPetImageUrl())
                .petBreed(delegateWalkPost.getProfile().getPetBreed())
                .extraInfo(delegateWalkPost.getProfile().getExtraInfo())
                .applicantCount(delegateWalkPost.getApplicants().size())
                .createdAt(TimeAgoUtil.getTimeAgo(delegateWalkPost.getCreatedAt()))
                .build();
    }
    public static boolean filter(DelegateWalkPost delegateWalkPost, Member member) {
        if (delegateWalkPost.isRequireProfile()) {
            if (member.getProfiles().size() != 0) {
                return false;
            }else
                return true;
        }else
            return false;
    }
}
