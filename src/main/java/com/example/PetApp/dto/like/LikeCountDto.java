package com.example.PetApp.dto.like;

import lombok.Getter;

@Getter
public class LikeCountDto {
    private Long postId;

    private Long likeCount;

    public LikeCountDto(Long postId, Long likeCount) {
        this.postId = postId;
        this.likeCount = likeCount;
    }
}
