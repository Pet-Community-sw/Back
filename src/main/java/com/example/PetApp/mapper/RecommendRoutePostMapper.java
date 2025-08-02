package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.domain.embedded.Location;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendPostResponseDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendRoutePostsResponseDto;
import com.example.PetApp.util.TimeAgoUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendRoutePostMapper {
    public static RecommendRoutePost  toEntity(CreateRecommendRoutePostDto createRecommendRoutePostDto, Member member) {
        return RecommendRoutePost.builder()
                .content(new Content(createRecommendRoutePostDto.getTitle(), createRecommendRoutePostDto.getContent()))
                .location(new Location(createRecommendRoutePostDto.getLocationLongitude(), createRecommendRoutePostDto.getLocationLatitude()))
                .member(member)
                .build();
    }

    public static List<GetRecommendRoutePostsResponseDto> toRecommendRoutePostsList(List<RecommendRoutePost> recommendRoutePosts,
                                                                                    Map<Long, Long> likeCountMap,
                                                                                    List<Long> likedRecommendPostIds,
                                                                                    Member member) {
        return recommendRoutePosts.stream()
                .map(recommendRoutePost -> GetRecommendRoutePostsResponseDto.builder()
                        .recommendRoutePostId(recommendRoutePost.getPostId())
                        .title(recommendRoutePost.getContent().getTitle())
                        .memberId(recommendRoutePost.getMember().getMemberId())
                        .memberName(recommendRoutePost.getMember().getName())
                        .memberImageUrl(recommendRoutePost.getMember().getMemberImageUrl())
                        .likeCount(likeCountMap.getOrDefault(recommendRoutePost.getPostId(), 0L))
                        .locationLongitude(recommendRoutePost.getLocation().getLocationLongitude())
                        .locationLatitude(recommendRoutePost.getLocation().getLocationLatitude())
                        .createdAt(TimeAgoUtil.getTimeAgo(recommendRoutePost.getCreatedAt()))
                        .isOwner(member.getMemberId().equals(recommendRoutePost.getMember().getMemberId()))
                        .isLike(likedRecommendPostIds.contains(recommendRoutePost.getPostId()))
                        .build()
                )
                .collect(Collectors.toList());

    }

    public static GetRecommendPostResponseDto toGetRecommendPostResponseDto(Member member, RecommendRoutePost post, Long likeCount, boolean isLike) {
        return GetRecommendPostResponseDto.builder()
                .recommendRoutePostId(post.getPostId())
                .title(post.getContent().getTitle())
                .content(post.getContent().getContent())
                .memberId(post.getMember().getMemberId())
                .memberName(post.getMember().getName())
                .memberImageUrl(post.getMember().getMemberImageUrl())
                .createdAt(TimeAgoUtil.getTimeAgo(post.getCreatedAt()))
                .likeCount(likeCount)
                .isOwner(post.getMember().getMemberId().equals(member.getMemberId()))
                .isLike(isLike)
                .build();
    }
}
