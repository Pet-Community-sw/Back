package com.example.PetApp.util;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.post.PostDto;

public class Mapper {
    public static Member createFakeMember() {
        return Member.builder()
                .memberId(1L)
                .name("최선재")
                .email("chltjswo789@naver.com")
                .password("fpdlswj365!")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
    }

    public static Member createFakeMember(Long memberId, String email) {
        return Member.builder()
                .memberId(memberId)
                .name("최선재")
                .email(email)
                .password("fpdlswj365!")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
    }

    public static PostDto toPostDto() {

        return PostDto.builder()
                .title("a")
                .content("b")
                .postImageFile(null)
                .build();
    }
}
