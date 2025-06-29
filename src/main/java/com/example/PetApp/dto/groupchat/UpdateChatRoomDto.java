package com.example.PetApp.dto.groupchat;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
public class UpdateChatRoomDto {

    private String chatRoomName;

    private int limitCount;
}
