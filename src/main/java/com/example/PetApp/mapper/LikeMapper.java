package com.example.PetApp.mapper;

import com.example.PetApp.domain.like.Like;
import com.example.PetApp.dto.like.LikeListDto;
import com.example.PetApp.dto.like.LikeResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class LikeMapper {
    public static List<LikeListDto> toLikeListDto(List<Like> likes) {
        return likes.stream()
                .map(like -> LikeListDto.builder()
                        .memberId(like.getMember().getMemberId())
                        .memberName(like.getMember().getName())
                        .memberImageUrl(like.getMember().getMemberImageUrl())
                        .build()
                ).collect(Collectors.toList());
    }

    public static LikeResponseDto toLikeResponseDto(List<Like> likes) {
        List<LikeListDto> likeListDto = toLikeListDto(likes);
        return LikeResponseDto.builder()
                .likeListDtos(likeListDto)
                .likeCount((long) likeListDto.size())
                .build();
    }

}
