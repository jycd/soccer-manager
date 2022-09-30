package com.toptal.soccermanager.utils;

import com.toptal.soccermanager.model.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamPair {
    Team team;
    double marketValue;
}
