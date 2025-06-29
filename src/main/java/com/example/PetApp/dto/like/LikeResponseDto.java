package com.example.PetApp.dto.like;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class LikeResponseDto {
    private List<LikeListDto> likeListDtos;

    private Long likeCount;


}
