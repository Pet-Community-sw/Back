package com.example.PetApp.dto.profile;

import com.example.PetApp.domain.DogBreed;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProfileResponseDto {
    private Long profileId;

    private String dogBreed;

    private String imageUrl;

    private Long memberId;

    private String dogName;

    private LocalDate dogBirthDate;

    private String dogAge;

    private Set<DogBreed> avoidBreeds;

    private boolean isOwner;
}
