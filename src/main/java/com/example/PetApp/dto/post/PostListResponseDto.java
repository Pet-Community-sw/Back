package com.example.PetApp.dto.post;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponseDto {

    private Long postId;

    private String postImageUrl;

    private Long memberId;

    private String memberName;

    private String timeAgo;

    private Long viewCount;

    private Long likeCount;

    private String title;

    private boolean like;

}
