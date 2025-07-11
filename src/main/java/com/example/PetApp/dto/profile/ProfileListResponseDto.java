package com.example.PetApp.dto.profile;


import lombok.*;
import org.springframework.stereotype.Service;
@Getter
@Builder
public class ProfileListResponseDto {

    private String petImageUrl;

    private String petName;

    private Long profileId;

    private boolean hasBirthday;

}
