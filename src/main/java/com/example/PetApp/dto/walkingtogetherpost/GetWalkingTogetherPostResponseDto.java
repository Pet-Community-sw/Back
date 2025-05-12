package com.example.PetApp.dto.walkingtogetherpost;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetWalkingTogetherPostResponseDto {
    private Long walkingTogetherPostId;

    private String content;

    private Long profileId;

    private String petName;

    private String petImageUrl;

    private int currentCount;

    private int limitCount;

    private String createdAt;

    private boolean isOwner;

    private boolean filtering;

}
