package com.example.PetApp.query;

import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.PetBreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetBreedQueryService {

    private final PetBreedRepository petBreedRepository;

    public PetBreed findByPetBreed(String petBreed) {
        return petBreedRepository.findByName(petBreed).orElseThrow(() -> new NotFoundException("종을 다시 입력해주세요."));
    }
}
