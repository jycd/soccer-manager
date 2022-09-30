package com.toptal.soccermanager.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerDto {
    private long id;
    private String firstName;
    private String lastName;
    private String country;
    private int age;
    private String position;
    private String marketValue;
    private TeamDto team;
    private TransferDto transfer;
}
