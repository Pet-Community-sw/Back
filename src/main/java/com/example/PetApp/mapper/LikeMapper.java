package com.example.PetApp.mapper;

import com.example.PetApp.dto.like.LikeListDto;
import com.example.PetApp.dto.like.LikeResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class LikeMapper {
    public static List<LikeListDto> toLikeListDto(List<LikeT> likes) {
        return likes.stream()
                .map(likeT -> LikeListDto.builder()
                        .memberName(likeT.getMember().getName())
                        .memberImageUrl(likeT.getMember().getMemberImageUrl())
                        .build()
                ).collect(Collectors.toList());
    }

    public static LikeResponseDto toLikeResponseDto(List<LikeListDto> likeListDtos) {
        return LikeResponseDto.builder()
                .likeListDtos(likeListDtos)
                .likeCount((long) likeListDtos.size())
                .build();
    }

}
