package com.example.PetApp.domain.embedded;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class Location {

    @NotNull
    private Double locationLongitude;

    @NotNull
    private Double locationLatitude;
}
