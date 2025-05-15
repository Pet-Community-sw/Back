package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelegateWalkPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long delegateWalkPostId;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private Long price;

    @NotEmpty
    private Double locationLongitude;

    @NotEmpty
    private Double locationLatitude;

    @NotEmpty
    private Integer allowedRadiusMeters;

    private boolean requireProfile;//profile여부 true or false

    private LocalDateTime scheduledTime;

    private LocalDateTime delegateWalkPostTime;

    @ElementCollection
    @CollectionTable(name = "walker_post_applicants")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Applicant> applicants = new HashSet<>();



}
