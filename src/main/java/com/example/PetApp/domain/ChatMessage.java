package com.example.PetApp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Document(collection = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    private String id;
    private MessageType messageType;
    private Long chatRoomId;
    private Long senderId;
    private String message;
    private LocalDateTime messageTime;//포맷 필요함.
}
