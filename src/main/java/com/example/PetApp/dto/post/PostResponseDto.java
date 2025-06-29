package com.example.PetApp.dto.post;

import lombok.*;

@Getter
@Builder
public class PostResponseDto {

    private Long postId;

    private String postImageUrl;

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    private String createdAt;

    private Long viewCount;

    private Long likeCount;

    private String title;

    private boolean like;

}
