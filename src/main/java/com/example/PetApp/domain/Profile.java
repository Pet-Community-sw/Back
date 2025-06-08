package com.example.PetApp.domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "profile")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @NotEmpty
    private String petImageUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate petBirthDate;

    @NotEmpty
    private String petAge;

    @NotEmpty//이것도 바꿔야할듯.
    private String petBreed;

    @NotEmpty
    private String petName;

    private String extraInfo;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profile_breed",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "pet_breed_id"))
    private Set<PetBreed> avoidBreeds = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "profile",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherPost> walkingTogetherPost;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatRoom> chatRooms;

    public void addAvoidBreeds(PetBreed dogBreed) {

        avoidBreeds.add(dogBreed);
    }
}
