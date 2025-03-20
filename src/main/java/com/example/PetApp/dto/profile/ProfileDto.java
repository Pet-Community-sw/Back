package com.example.PetApp.dto.profile;

import com.example.PetApp.domain.DogBreed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private MultipartFile file;

    private String dogBreed;

    private String dogName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dogBirthDate;

    private String avoidBreeds;

    private String extraInfo;

}
