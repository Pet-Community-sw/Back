package com.example.PetApp.dto.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreatePostDto {
    private String title;

    private String content;

    private Long profileId;

    MultipartFile postImageFile;
}
