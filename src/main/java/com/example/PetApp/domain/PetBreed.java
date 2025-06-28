package com.example.PetApp.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "pet_breed")
@Builder
public class PetBreed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petBreedId;

    @NotBlank
    @Column(nullable = false, updatable = false)
    private String name;

}
