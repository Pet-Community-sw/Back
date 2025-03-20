package com.example.PetApp.service;

import com.example.PetApp.domain.DogBreed;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public interface DogBreedService {

    Optional<DogBreed> findByName(String name);

    Optional<DogBreed> findById(Long id);

}
