package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Profile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String petImageUrl;

    @Setter
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(nullable = false)
    private LocalDate petBirthDate;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String petAge;

    @Setter
    @NotBlank
    @JoinColumn(name = "pet_breed_id", nullable = false)
    @OneToOne
    private PetBreed petBreed;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String petName;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String extraInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @Builder.Default
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "profile_breed",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "pet_breed_id"))
    private Set<PetBreed> avoidBreeds = new HashSet<>();

    @OneToMany(mappedBy = "profile",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherPost> walkingTogetherPost;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<ChatRoom> chatRooms;

    public void addAvoidBreeds(PetBreed dogBreed) {

        avoidBreeds.add(dogBreed);
    }
}
