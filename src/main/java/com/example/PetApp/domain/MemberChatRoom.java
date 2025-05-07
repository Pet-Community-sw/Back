package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberChatRoomId;

    @OneToMany
    private List<Member> members;

    @CreationTimestamp
    private LocalDateTime memberChatRoomTime;
}
