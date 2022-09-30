package com.toptal.soccermanager.model.dto;

import com.toptal.soccermanager.model.entity.Role;
import com.toptal.soccermanager.utils.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Size(max = 60)
    private String fullName;

    @NotNull
    @EnumValidator(enumClazz = Role.class)
    private String role;
}
