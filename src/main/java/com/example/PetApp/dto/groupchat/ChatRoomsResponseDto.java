package com.example.PetApp.dto.groupchat;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.ChatRoomProfilesResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomsResponseDto {

    private Long chatRoomId;

    private String chatName;

    private int chatLimitCount;

    private int currentCount;

    private LocalDateTime chatRoomTime;

    private Set<ChatRoomProfilesResponseDto> profiles = new HashSet<>();

    private String lastMessage;

    private int unReadCount;

    private LocalDateTime lastMessageTime;

    private boolean isOwner;


}
