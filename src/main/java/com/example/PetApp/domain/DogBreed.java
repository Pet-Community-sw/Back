package com.example.PetApp.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dogbreed")
@Builder
public class DogBreed {

    @Id
    @Column(name = "dog_breed_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dogBreedId;

    private String name;

}
