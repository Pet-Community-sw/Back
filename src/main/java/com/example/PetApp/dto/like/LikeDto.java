package com.example.PetApp.dto.like;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //좋아요 목록도 db에 저장이 되어야하네 이런 시발.
public class LikeDto {

    private Long postId;

}
