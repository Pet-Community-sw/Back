package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chatRoom")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String name;

    private int limitCount;

    @CreationTimestamp
    private LocalDateTime regdate;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;//이게 맞는지

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "chat_id"),
    inverseJoinColumns = @JoinColumn(name = "chat_room_id"))
    @Builder.Default//초기화 시켜줌. 빌더가 안먹힘.
    private Set<Profile> profiles = new HashSet<>();

    public void addProfiles(Profile profile) {
        this.profiles.add(profile);
    }
}
