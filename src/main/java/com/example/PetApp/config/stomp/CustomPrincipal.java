package com.example.PetApp.config.stomp;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class CustomPrincipal implements Principal {
    @Getter
    @Setter
    private Long profileId;


    @Override
    public String getName() {
        return null;
    }
}
