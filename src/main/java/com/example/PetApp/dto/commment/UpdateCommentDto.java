package com.example.PetApp.dto.commment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDto {
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
