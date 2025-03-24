package com.example.PetApp.dto.post;

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
