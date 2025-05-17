package com.example.PetApp.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalkRecord {

    public enum WalkStatus {
        READY, START, FINISH, CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkRecordId;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private Double walkDistance;

    @Enumerated(EnumType.STRING)
    private WalkStatus walkStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_walk_post")
    private DelegateWalkPost delegateWalkPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
