package com.example.PetApp.dto.profile;


import lombok.*;
import org.springframework.stereotype.Service;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileListResponseDto {

    private String imageUrl;

    private String petName;

    private Long profileId;

    private boolean hasBirthday;

}
