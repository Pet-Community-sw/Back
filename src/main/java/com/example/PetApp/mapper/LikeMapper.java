package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.like.Like;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.dto.like.LikeListDto;
import com.example.PetApp.dto.like.LikeResponseDto;

import java.util.List;

public class LikeMapper {

    public static Like toEntity(Member member, Post post) {
        return Like.builder()
                .member(member)
                .post(post)
                .build();
    }

    public static LikeResponseDto toLikeResponseDto(List<LikeListDto> likeListDtos) {
        return LikeResponseDto.builder()
                .likeListDtos(likeListDtos)
                .likeCount((long) likeListDtos.size())
                .build();
    }

}
