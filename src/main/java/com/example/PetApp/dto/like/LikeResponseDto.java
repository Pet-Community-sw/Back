package com.example.PetApp.dto.like;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDto {
    private List<LikeListDto> likeListDtos;

    private Long likeCount;


}
