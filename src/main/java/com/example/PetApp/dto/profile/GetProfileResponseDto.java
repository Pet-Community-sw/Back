package com.example.PetApp.dto.profile;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProfileResponseDto {
    private Long profileId;

    private String dogBreed;

    private String imageUrl;

    private Long memberId;

    private String name;

    private boolean isOwner;
}
