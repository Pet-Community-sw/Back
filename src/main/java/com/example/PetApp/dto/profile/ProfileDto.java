package com.example.PetApp.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private MultipartFile petImageUrl;

    private String petBreed;

    private String petName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate petBirthDate;

    private String avoidBreeds;

    private String extraInfo;

}
