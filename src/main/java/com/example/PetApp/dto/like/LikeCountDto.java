package com.example.PetApp.dto.like;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCountDto {
    private Long postId;

    private Long likeCount;
}
