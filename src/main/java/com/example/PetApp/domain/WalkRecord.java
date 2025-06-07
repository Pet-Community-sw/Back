package com.example.PetApp.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "walk_path_points", joinColumns = @JoinColumn(name = "walk_record_id"))
    @Column(name = "point")
    private List<String> pathPoints = new ArrayList<>();


}
