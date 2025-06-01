package com.example.PetApp.dto.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginDto {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

}
