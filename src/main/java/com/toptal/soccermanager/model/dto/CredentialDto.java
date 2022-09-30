package com.toptal.soccermanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
