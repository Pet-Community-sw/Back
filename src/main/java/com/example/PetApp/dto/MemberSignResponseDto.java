package com.example.PetApp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSignResponseDto {

    private Long memberId;

    private String name;
}
