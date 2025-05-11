package com.example.PetApp.dto.walkerpost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter//나중에 장소 변경가능하도록
public class UpdateWalkerPostDto {

    private Long walkerPostId;

    private int level;

    private Long price;
}
