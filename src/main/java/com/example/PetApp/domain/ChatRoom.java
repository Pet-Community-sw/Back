package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.PageRequest;

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
    private LocalDateTime chatRoomTime;

    @OneToOne
    @JoinColumn(name = "match_post_id")
    private MatchPost matchPost;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id"))
    @Builder.Default//초기화 시켜줌. 빌더가 안먹힘.
    private List<Profile> profiles=new ArrayList<>();

    public void addProfiles(Profile profile) {
        profiles.add(profile);
    }
}