package com.example.PetApp.dto.recommendroutepost;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private boolean isLike;
}
