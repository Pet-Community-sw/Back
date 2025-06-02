package com.example.PetApp.util.imagefile;

import lombok.Getter;
import lombok.Setter;

public enum FileImageKind {
    POST("posts"),
    MEMBER("members"),
    PROFILE("profiles");

    FileImageKind(String type) {
        this.type = type;
    }

    @Getter
    @Setter
    private String type;
    
}
