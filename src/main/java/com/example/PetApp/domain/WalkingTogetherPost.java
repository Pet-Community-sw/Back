package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "walking_together_post")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class WalkingTogetherPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkingTogetherPostId;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int limitCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_route_post_id")
    private RecommendRoutePost recommendRoutePost;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime walkingTogetherPostTime;

    @ElementCollection
    @CollectionTable(name = "walking_together_post_profiles")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Long> profiles=new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "walking_together_post_avoid_Breeds")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Long> avoidBreeds=new HashSet<>();

    public void addMatchPostProfiles(Long profileId) {
        this.profiles.add(profileId);
    }

    public void addAvoidBreeds(Profile profile) {
        profile.getAvoidBreeds().forEach(avoidBreeds -> this.avoidBreeds.add(avoidBreeds.getPetBreedId()));
    }
}


