package com.example.PetApp.dto.member;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
public class LoginDto {

    private String email;

    private String password;

}
