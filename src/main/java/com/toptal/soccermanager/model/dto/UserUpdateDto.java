package com.toptal.soccermanager.model.dto;

import com.toptal.soccermanager.model.entity.Role;
import com.toptal.soccermanager.model.entity.Status;
import com.toptal.soccermanager.utils.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @Email
    private String email;

    private String password;

    @Size(max = 60)
    private String fullName;

    @EnumValidator(enumClazz = Role.class)
    private String role;

    @EnumValidator(enumClazz = Status.class)
    private String status;
}
