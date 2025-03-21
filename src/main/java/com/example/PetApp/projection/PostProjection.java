package com.example.PetApp.projection;

import com.example.PetApp.domain.Profile;

public interface PostProjection {

    Profile getProfile();
    String getTitle();

    String getContent();

    int getLikeCount();
}
