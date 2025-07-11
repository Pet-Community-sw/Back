package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Table
@Entity
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DelegateWalkPost {
    public enum DelegateWalkStatus {
        RECRUITING,   // 모집중
        COMPLETED     // 모집완료
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long delegateWalkPostId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String title;

    @Setter
    @NotBlank    @Column(nullable = false)
    private String content;

    @Setter
    @Min(0)
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long price;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Double locationLongitude;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Double locationLatitude;

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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime delegateWalkPostTime;

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
