package com.example.PetApp.dto.profile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomProfilesResponseDto {

    private Long profileId;

    private String profileImageUrl;
}
