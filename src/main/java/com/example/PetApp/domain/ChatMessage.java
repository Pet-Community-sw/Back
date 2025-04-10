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
    private Long SenderId;
    private String senderName;
    private String message;
    private LocalDateTime localDateTime;//포맷 필요함.
    private ChatRoom chatRoom;//ChatRoom과 관계설정을 할 수 있을까?
}
