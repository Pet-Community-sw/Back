package com.example.PetApp.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
        ENTER, TALK, LEAVE
    }

    @Id
    private String id;
    private MessageType messageType;
    private Long chatRoomId;
    private Long senderId;
    private String message;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime messageTime;//포맷 필요함.

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", messageType=" + messageType +
                ", chatRoomId=" + chatRoomId +
                ", senderId=" + senderId +
                ", message='" + message + '\'' +
                ", messageTime=" + messageTime +
                '}';
    }
}
