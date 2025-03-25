package com.example.PetApp.dto.like;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLikeDto {

    private Long profileId;

    private Long postId;

    private boolean isLike;
}
