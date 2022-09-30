package com.toptal.soccermanager.model.dto;

import com.toptal.soccermanager.model.entity.Position;
import com.toptal.soccermanager.utils.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCreateDto {
    @NotBlank
    @Size(max = 30)
    private String firstName;

    @NotBlank
    @Size(max = 30)
    private String lastName;

    @NotBlank
    @Size(max = 60)
    private String country;

    @Min(18)
    @Max(40)
    private int age;

    @NotNull
    @EnumValidator(enumClazz = Position.class)
    private String position;

    @Min(0)
    private double marketValue;
}
