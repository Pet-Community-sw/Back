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
    private String imageUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dogBirthDate;

    @NotEmpty
    private String dogAge;

    @NotEmpty
    private String dogBreed;

    @NotEmpty
    private String dogName;

    private String extraInfo;

    @ManyToMany
    @JoinTable(name = "profile_breed",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "dog_breed_id"))
    private Set<DogBreed> avoidBreeds = new HashSet<>();

    @JoinColumn(name = "member_id")
    private Long memberId;

    @OneToMany(mappedBy = "profile",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeT> likeTs;

    public void addAvoidBreeds(DogBreed dogBreed) {

        avoidBreeds.add(dogBreed);
    }
}
