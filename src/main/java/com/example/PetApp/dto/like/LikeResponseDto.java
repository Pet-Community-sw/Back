package com.example.PetApp.dto.like;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.commment.LikeListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class LikeResponseDto {
    private List<LikeListDto> likeListDtos;

    private Long likeCount;


}
