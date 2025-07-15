package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.embedded.Location;
import com.example.PetApp.domain.embedded.PostContent;
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
                .postContent(new PostContent(createRecommendRoutePostDto.getTitle(), createRecommendRoutePostDto.getContent()))
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
                        .recommendRoutePostId(recommendRoutePost.getRecommendRouteId())
                        .title(recommendRoutePost.getPostContent().getTitle())
                        .memberId(recommendRoutePost.getMember().getMemberId())
                        .memberName(recommendRoutePost.getMember().getName())
                        .memberImageUrl(recommendRoutePost.getMember().getMemberImageUrl())
                        .likeCount(likeCountMap.getOrDefault(recommendRoutePost.getRecommendRouteId(), 0L))
                        .locationLongitude(recommendRoutePost.getLocation().getLocationLongitude())
                        .locationLatitude(recommendRoutePost.getLocation().getLocationLatitude())
                        .createdAt(TimeAgoUtil.getTimeAgo(recommendRoutePost.getCreatedAt()))
                        .isOwner(member.getMemberId().equals(recommendRoutePost.getMember().getMemberId()))
                        .isLike(likedRecommendPostIds.contains(recommendRoutePost.getRecommendRouteId()))
                        .build()
                )
                .collect(Collectors.toList());

    }

    public static GetRecommendPostResponseDto toGetRecommendPostResponseDto(Member member, RecommendRoutePost post, Long likeCount, boolean isLike) {
        return GetRecommendPostResponseDto.builder()
                .recommendRoutePostId(post.getRecommendRouteId())
                .title(post.getPostContent().getTitle())
                .content(post.getPostContent().getContent())
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
