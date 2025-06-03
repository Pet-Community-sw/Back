package com.example.PetApp.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ChatRoomProfilesResponseDto {

    private Long profileId;

    private String profileImageUrl;
}
