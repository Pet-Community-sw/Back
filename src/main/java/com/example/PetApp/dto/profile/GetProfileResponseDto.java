package com.example.PetApp.dto.profile;

import com.example.PetApp.domain.PetBreed;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetProfileResponseDto {
    private Long profileId;

    private String petBreed;

    private String petImageUrl;

    private Long memberId;

    private String petName;

    private LocalDate petBirthDate;

    private String petAge;

    private Set<PetBreed> avoidBreeds;

    private boolean isOwner;
}
