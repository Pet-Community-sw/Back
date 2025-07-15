package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int limitCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walking_together_post_id")
    private WalkingTogetherPost walkingTogetherPost;

    @Setter
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id"))
    @Builder.Default
    private List<Profile> profiles=new ArrayList<>();

    public void addProfiles(Profile profile) {
        profiles.add(profile);
    }
}