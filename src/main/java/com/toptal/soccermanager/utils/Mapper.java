package com.toptal.soccermanager.utils;

import com.toptal.soccermanager.model.dto.PlayerDto;
import com.toptal.soccermanager.model.dto.TeamDto;
import com.toptal.soccermanager.model.dto.TransferDto;
import com.toptal.soccermanager.model.dto.UserDto;
import com.toptal.soccermanager.model.entity.Player;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.model.entity.Transfer;
import com.toptal.soccermanager.model.entity.User;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

@Component
public class Mapper {
    public UserDto getUserDtoFromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());

        return dto;
    }

    public TeamDto getTeamDtoFromEntity(Team team, boolean withMarketValue, boolean withUser, boolean withPlayers) {
        TeamDto dto = new TeamDto();

        dto.setId(team.getId());
        dto.setCountry(team.getCountry());
        dto.setName(team.getName());
        dto.setBudget(formatFloatNumber(team.getBudget()));
        if (withMarketValue) {
            dto.setMarketValue(formatFloatNumber(team.getMarketValue()));
        }

        if (withUser) {
            dto.setUser(getUserDtoFromEntity(team.getUser()));
        }

        if (withPlayers) {
            dto.setPlayers(team.getPlayers().stream()
                    .map(p -> getPlayerDtoFromEntity(p, false, true))
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
    public TeamDto getTeamDtoFromEntity(Team team, double marketValue) {
        TeamDto dto = new TeamDto();

        dto.setId(team.getId());
        dto.setCountry(team.getCountry());
        dto.setName(team.getName());
        dto.setBudget(formatFloatNumber(team.getBudget()));
        dto.setMarketValue(formatFloatNumber(marketValue));

        return dto;
    }

    public PlayerDto getPlayerDtoFromEntity(Player player, boolean withTeam, boolean withTransfer) {
        PlayerDto dto = new PlayerDto();
        dto.setId(player.getId());
        dto.setFirstName(player.getFirstName());
        dto.setLastName(player.getLastName());
        dto.setCountry(player.getCountry());
        dto.setAge(player.getAge());
        dto.setPosition(player.getPosition().name());
        dto.setMarketValue(formatFloatNumber(player.getMarketValue()));

        if (withTeam) {
            dto.setTeam(getTeamDtoFromEntity(player.getTeam(), false, false, false));
        }

        if (withTransfer && player.getTransfer() != null) {
            dto.setTransfer(getTransferDtoFromEntity(player.getTransfer(), false));
        }

        return dto;
    }

    public TransferDto getTransferDtoFromEntity(Transfer transfer, boolean withPlayer) {
        TransferDto dto = new TransferDto();
        dto.setId(transfer.getId());
        dto.setAskPrice(formatFloatNumber(transfer.getAskPrice()));
        if (withPlayer) {
            dto.setPlayer(getPlayerDtoFromEntity(transfer.getPlayer(), true, false));
        }

        return dto;
    }

    private String formatFloatNumber(double value) {
        if (value != 0.00) {
            java.text.DecimalFormat df = new DecimalFormat("########.00");
            return df.format(value);
        } else {
            return "0.00";
        }
    }

    private String formatFloatNumber(Double value) {
        if (value != null) {
            if (value != 0.00) {
                java.text.DecimalFormat df = new DecimalFormat("########.00");
                return df.format(value.doubleValue());
            } else {
                return "0.00";
            }
        }
        return "";
    }
}
