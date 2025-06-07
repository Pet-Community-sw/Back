package com.example.PetApp.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class LoginDto {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

}
