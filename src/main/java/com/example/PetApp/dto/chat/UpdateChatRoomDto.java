package com.example.PetApp.dto.chat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChatRoomDto {

    private Long chatRoomId;

    private Long profileId;

    private String chatRoomTitle;

    private int limitCount;
}
