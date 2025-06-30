package com.example.PetApp.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailDto {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}
