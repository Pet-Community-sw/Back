package com.example.PetApp.dto.recommendroutepost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GetRecommendRoutePostsResponseDto {

    private Long recommendRoutePostId;

    private String title;

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    private Long likeCount;

    private Double locationLongitude;

    private Double locationLatitude;

    private String createdAt;

    private boolean isOwner;

    private boolean isLiked;
}
