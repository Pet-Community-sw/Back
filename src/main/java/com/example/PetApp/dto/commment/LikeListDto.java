package com.example.PetApp.dto.commment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LikeListDto {

    private String memberName;
    private String memberImageUrl;
}
