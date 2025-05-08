package com.example.PetApp.dto.groupchat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChatRoomDto {

    private Long chatRoomId;

    private String chatRoomTitle;

    private int limitCount;
}
