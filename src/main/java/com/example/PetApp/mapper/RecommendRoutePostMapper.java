package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendPostResponseDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendRoutePostsResponseDto;
import com.example.PetApp.util.TimeAgoUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendRoutePostMapper {
    public static RecommendRoutePost  toEntity(CreateRecommendRoutePostDto createRecommendRoutePostDto, Member member) {
        return RecommendRoutePost.builder()
                .title(createRecommendRoutePostDto.getTitle())
                .content(createRecommendRoutePostDto.getContent())
                .locationLongitude(createRecommendRoutePostDto.getLocationLongitude())
                .locationLatitude(createRecommendRoutePostDto.getLocationLatitude())
                .member(member)
                .build();
    }

    public static List<GetRecommendRoutePostsResponseDto> toRecommendRoutePostsList(List<RecommendRoutePost> recommendRoutePosts,
                                                                                    Map<Long, Long> likeCountMap,
                                                                                    List<Long> likedRecommendPostIds,
                                                                                    Member member) {
        return recommendRoutePosts.stream()
                .map(recommendRoutePost -> GetRecommendRoutePostsResponseDto.builder()
                        .recommendRoutePostId(recommendRoutePost.getRecommendRouteId())
                        .title(recommendRoutePost.getTitle())
                        .memberId(recommendRoutePost.getMember().getMemberId())
                        .memberName(recommendRoutePost.getMember().getName())
                        .memberImageUrl(recommendRoutePost.getMember().getMemberImageUrl())
                        .likeCount(likeCountMap.getOrDefault(recommendRoutePost.getRecommendRouteId(), 0L))
                        .locationLongitude(recommendRoutePost.getLocationLongitude())
                        .locationLatitude(recommendRoutePost.getLocationLatitude())
                        .createdAt(TimeAgoUtil.getTimeAgo(recommendRoutePost.getRecommendRouteTime()))
                        .isOwner(member.getMemberId().equals(recommendRoutePost.getMember().getMemberId()))
                        .isLike(likedRecommendPostIds.contains(recommendRoutePost.getRecommendRouteId()))
                        .build()
                )
                .collect(Collectors.toList());

    }

    public static GetRecommendPostResponseDto toGetRecommendPostResponseDto(Member member, RecommendRoutePost post, Long likeCount, boolean isLike) {
        return GetRecommendPostResponseDto.builder()
                .recommendRoutePostId(post.getRecommendRouteId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberId(post.getMember().getMemberId())
                .memberName(post.getMember().getName())
                .memberImageUrl(post.getMember().getMemberImageUrl())
                .createdAt(TimeAgoUtil.getTimeAgo(post.getRecommendRouteTime()))
                .likeCount(likeCount)
                .isOwner(post.getMember().getMemberId().equals(member.getMemberId()))
                .isLike(isLike)
                .build();
    }
}
