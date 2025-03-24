package com.example.PetApp.dto.post;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //좋아요 목록도 db에 저장이 되어야하네 이런 시발.
public class UpdateLikeResponseDto {

    private Long likeCount;

}
