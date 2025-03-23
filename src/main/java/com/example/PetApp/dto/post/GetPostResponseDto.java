package com.example.PetApp.dto.post;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostResponseDto {

    private Long postId;

    private String title;

    private String content;

    private String imageUrl;

    private Long viewCount;

    private Long likeCount;

    private Long profileId;

    private String profileName;

    private String profileImageUrl;

    List<PostListResponseDto> comments;

}
