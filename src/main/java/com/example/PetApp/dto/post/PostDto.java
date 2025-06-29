package com.example.PetApp.dto.post;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;


    MultipartFile postImageFile;
}
