package com.example.PetApp.firebase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmRequestDTO {
    private String targetToken;
    private String title;
    private String body;
}