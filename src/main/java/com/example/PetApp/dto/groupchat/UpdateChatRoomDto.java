package com.example.PetApp.dto.groupchat;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChatRoomDto {

    @NotBlank(message = "채팅방 이름은 필수입니다.")
    private String chatRoomName;

    @NotNull(message = "제한 인원수는 필수입니다.")
    private int limitCount;
}
