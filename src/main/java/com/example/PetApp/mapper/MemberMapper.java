package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.MemberSignDto;

public class MemberMapper {

    public static Member toEntity(MemberSignDto memberSignDto, String encodedPassword, String imageFileName) {
        return Member.builder()
                .name(memberSignDto.getName())
                .email(memberSignDto.getEmail())
                .password(encodedPassword)
                .phoneNumber(memberSignDto.getPhoneNumber())
                .memberImageUrl(imageFileName)
                .build();
    }

}
