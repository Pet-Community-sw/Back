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

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;//이게 맞는지

    @OneToMany
    @Builder.Default//초기화 시켜줌. 빌더가 안먹힘.
    private Set<Profile> profiles = new HashSet<>();

    public void addProfiles(Profile profile) {
        this.profiles.add(profile);
    }
}
