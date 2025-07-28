package com.example.PetApp.dto.like;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeListDto {

    private Long memberId;
    private String memberName;
    private String memberImageUrl;
}
