package com.example.PetApp.dto.profile;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

    @NotBlank
    private MultipartFile petImageUrl;

    @NotBlank
    private String petBreed;

    @NotBlank
    private String petName;//이거 연결해야함.

    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate petBirthDate;

    private String avoidBreeds;

    private String extraInfo;

}
