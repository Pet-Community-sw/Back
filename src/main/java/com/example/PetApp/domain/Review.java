package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

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

    private String title;

    private String content;

    private Integer rating;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    @CreationTimestamp
    private LocalDateTime reviewTime;
}
