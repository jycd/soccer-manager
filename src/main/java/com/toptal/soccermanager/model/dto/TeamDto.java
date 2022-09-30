package com.toptal.soccermanager.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDto {
    private long id;
    private String name;
    private String country;
    private String marketValue;
    private String budget;
    private UserDto user;
    private Set<PlayerDto> players;
}
