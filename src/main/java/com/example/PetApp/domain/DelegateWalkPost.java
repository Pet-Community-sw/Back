package com.example.PetApp.domain;

import com.example.PetApp.domain.embedded.Applicant;
import com.example.PetApp.domain.embedded.Location;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DelegateWalkPost extends BaseTimeEntity {
    public enum DelegateWalkStatus {
        RECRUITING,   // 모집중
        COMPLETED     // 모집완료
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long delegateWalkPostId;

    @Setter
    @Embedded
    private Content content;

    @Setter
    @Embedded
    private Location location;

    @Setter
    @Min(0)
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long price;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer allowedRadiusMeters;

    @Setter
    @NotEmpty
    @Column(nullable = false)
    private Long selectedApplicantMemberId;

    @Setter
    @NotNull
    @Column(nullable = false)
    private boolean requireProfile;//profile여부 true or false

    @Setter
    @NotNull
    @Column(nullable = false)
    private boolean startAuthorized;// start권한 부여

    @Setter
    @NotEmpty
    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DelegateWalkStatus status = DelegateWalkStatus.RECRUITING;//기본값을 모집중으로 선언.

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "walker_post_applicants")
    private Set<Applicant> applicants = new HashSet<>();

}
