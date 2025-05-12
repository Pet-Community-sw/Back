package com.example.PetApp.dto.recommendroutepost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor//코드 중복임 리팩토링 시 수정해야됨.
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

    private boolean isLiked;
}
