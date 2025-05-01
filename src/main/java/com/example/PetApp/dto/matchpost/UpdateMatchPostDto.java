package com.example.PetApp.dto.matchpost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMatchPostDto {

    private Long matchPostId;

    private String content;

    private int limitCount;

    //장소 추가해야할듯.
}
