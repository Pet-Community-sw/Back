package com.example.PetApp.dto.recommendroutepost;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRecommendPostResponseDto {
    private Long recommendRoutePostId;

    private String title;

    private String content;

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    private String createdAt;

    private Long likeCount;

    private boolean isOwner;

    private boolean isLike;
}
