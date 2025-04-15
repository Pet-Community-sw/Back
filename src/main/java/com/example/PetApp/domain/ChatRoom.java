package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chatRoom")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String name;

    private int limitCount;

    @CreationTimestamp
    private LocalDateTime regdate;

    @OneToOne()
    @JoinColumn(name = "post_id")//chatroom이 사라지면 같이 post도 삭제가됨.
    private Post post;//이게 맞는지

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id"))
    @Builder.Default//초기화 시켜줌. 빌더가 안먹힘.
    private List<Profile> profiles=new ArrayList<>();

    public void addProfiles(Profile profile) {
        profiles.add(profile);
    }
}