package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberChatRoomId;

    @Builder.Default//이미 초기화가 되어있기때문에 notnull이 필요 없음.
    @Setter
    @OneToMany(fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime memberChatRoomTime;
}
