package com.example.PetApp.dto.memberchat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberChatRoomsResponseDto {

    private String chatName;//상대 memberName

    private String chatImageUrl;//상대 memberImageUrl

    private String lastMessage;

    private int unReadCount;

    private LocalDateTime lastMessageTime;

}
