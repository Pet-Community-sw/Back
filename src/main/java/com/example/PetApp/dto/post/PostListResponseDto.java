package com.example.PetApp.dto.post;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponseDto {

    private Long postId;

    private Long profileId;

    private String profileName;

    private String profileImageUrl;

    private String title;

    private String timeAgo;

    private Long viewCount;

    private Long likeCount;

}
