package com.example.PetApp.config.stomp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.example.PetApp.domain.ChatMessage.*;

@Getter
@Setter
@AllArgsConstructor
public class SubscribeInfo {

    private Long chatRoomId;

    private String userId;

    private ChatRoomType chatRoomType;
}
