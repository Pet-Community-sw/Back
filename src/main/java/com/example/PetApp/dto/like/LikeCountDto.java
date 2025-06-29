package com.example.PetApp.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeCountDto {
    private Long postId;

    private Long likeCount;
}
