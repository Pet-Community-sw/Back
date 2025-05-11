package com.example.PetApp.dto.walkerpost;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class CreateWalkerPostDto {
    private String title;

    private Long profileId;

    private String content;

    private Long price;

    private Double minLongitude;

    private Double minLatitude;

    private Double maxLongitude;

    private Double maxLatitude;

    private Double locationLongitude;

    private Double locationLatitude;

    private int level;
}
