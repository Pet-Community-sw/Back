package com.example.PetApp.dto.like;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeListDto {

    private Long memberId;
    private String memberName;
    private String memberImageUrl;
}
