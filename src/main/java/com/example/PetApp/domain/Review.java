package com.example.PetApp.domain;

import com.example.PetApp.domain.embedded.PostContent;
import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Entity
@Table
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
    private PostContent postContent;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;
}
