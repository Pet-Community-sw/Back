package com.example.PetApp.domain;

import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Review extends BaseTimeEntity {

    public enum ReviewType {
        MEMBER_TO_PROFILE, PROFILE_TO_MEMBER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "walk_record_id")
    private WalkRecord walkRecord;

    @Setter
    @Embedded
    private Content content;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;
}
