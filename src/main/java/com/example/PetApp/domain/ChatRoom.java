package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat")
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
    private LocalDateTime localDateTime;

    @OneToOne(mappedBy = "chatRoomId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Post post;//이게 맞는지

    @OneToMany
    private List<Profile> profiles = new ArrayList<>();

    public void addProfiles(Profile profile) {
        this.profiles.add(profile);
    }

}
