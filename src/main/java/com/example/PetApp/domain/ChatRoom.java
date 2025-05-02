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
    @Builder.Default//빌더는 생성자를 this.profiles=profiles라 해서 리스틑 초기화가안됨 그래서 default로 설정.
    private List<Profile> profiles=new ArrayList<>();

    public void addProfiles(Profile profile) {
        profiles.add(profile);
    }
}