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

    private Long profileId;

    private String profileName;

    private String profileImageUrl;

    private String title;

    private String timeAgo;

    private Long viewCount;

    private Long likeCount;

    public PostListResponseDto(Long postId, Long commentId, String content, String imageUrl, String timeAgo, String dogName, Long likeCount, Long postId1) {
        this.postId = postId;
    }
}
