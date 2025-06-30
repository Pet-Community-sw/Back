package com.example.PetApp.dto.profile;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

    @NotNull(message = "반려견 이미지는 필수입니다.")
    private MultipartFile petImageUrl;

    @NotBlank(message = "반려견 종이름은 필수입니다.")
    private String petBreed;

    @NotBlank(message = "반려견 이름은 필수입니다.")
    private String petName;//이거 연결해야함.

    @NotNull(message = "반려견 생일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate petBirthDate;

    private String avoidBreeds;

    private String extraInfo;

}
