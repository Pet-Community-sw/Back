package com.example.PetApp.dto.profile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProfileResponseDto {
    private Long profileId;

    private String imageUrl;

    private String dogBreed;

    private String name;

    private Long memberId;
}
