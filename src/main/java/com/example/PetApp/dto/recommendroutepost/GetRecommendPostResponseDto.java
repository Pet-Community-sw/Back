package com.example.PetApp.dto.recommendroutepost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
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
