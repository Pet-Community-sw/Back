package com.example.PetApp.dto.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class PostDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;


    MultipartFile postImageFile;
}
