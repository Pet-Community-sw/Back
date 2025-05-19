package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "walking_together_post")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalkingTogetherPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkingTogetherPostId;

    private int limitCount;

    @OneToOne
    @JoinColumn(name = "recommend_route_post_id")
    private RecommendRoutePost recommendRoutePost;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    private LocalDateTime scheduledTime;

    @CreationTimestamp
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


