package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class WalkRecord extends BaseTimeEntity {

    public enum WalkStatus {
        READY, START, FINISH, CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkRecordId;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime finishTime;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Double walkDistance;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalkStatus walkStatus=WalkStatus.READY;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_walk_post")
    private DelegateWalkPost delegateWalkPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "walk_path_points", joinColumns = @JoinColumn(name = "walk_record_id"))
    @Column(name = "point")
    private List<String> pathPoints = new ArrayList<>();


}
