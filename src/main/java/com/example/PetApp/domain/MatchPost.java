package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "match_post")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchPostId;

    private String content;

    private Double latitude;

    private Double longitude;

    private String locationName;

    private int limitCount;

    @ManyToOne(cascade = CascadeType.ALL)
    private Profile profile;

    @CreationTimestamp
    private LocalDateTime matchPostTime;

    @ElementCollection
    @CollectionTable(name = "match_post_prifles",
            joinColumns = @JoinColumn(name = "match_post_id"))
    private Set<Long> profiles;

    @ElementCollection
    @CollectionTable(name = "match_post_avoid_Breeds",
            joinColumns = @JoinColumn(name = "match_post_id"))
    private Set<Long> avoidBreeds;

    public void addMatchPostProfiles(Long profileId) {
        this.profiles.add(profileId);
    }

    public void addAvoidBreeds(Long avoidBreedsId) {
        this.avoidBreeds.add(avoidBreedsId);
    }
}


