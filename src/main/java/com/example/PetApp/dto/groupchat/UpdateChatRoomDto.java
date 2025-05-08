package com.example.PetApp.dto.groupchat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChatRoomDto {

    private String chatRoomName;

    private int limitCount;
}
