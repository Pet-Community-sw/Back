package com.example.PetApp.dto;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;

    private String chatName;

    private int chatLimitCount;

    private int currentCount;

    private LocalDateTime regDate;

    private Set<Long> profiles=new HashSet<>();


    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatName(chatRoom.getName())
                .chatLimitCount(chatRoom.getLimitCount())
                .currentCount(chatRoom.getProfiles().size())
                .regDate(chatRoom.getLocalDateTime())
                .profiles(chatRoom.getProfiles().stream()
                        .map(Profile::getProfileId) // Profile → ID만 추출
                        .collect(Collectors.toSet()))
                .build();
    }
//    private int profileCount;
//
//    private int unReadCount;
//
//    private String lastMessage;
//
//    private LocalDateTime lastMessageTime;
}
