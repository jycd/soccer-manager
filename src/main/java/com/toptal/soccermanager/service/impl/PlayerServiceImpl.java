package com.toptal.soccermanager.service.impl;

import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Player;
import com.toptal.soccermanager.model.entity.Position;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.repository.PlayerRepository;
import com.toptal.soccermanager.repository.TeamRepository;
import com.toptal.soccermanager.service.PlayerService;
import com.toptal.soccermanager.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private Mapper mapper;

    @Override
    @Transactional
    public PlayerDto create(PlayerCreateDto player, long teamId) throws ApplicationException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        Player newPlayer = new Player();
        newPlayer.setFirstName(player.getFirstName());
        newPlayer.setLastName(player.getLastName());
        newPlayer.setCountry(player.getCountry());
        newPlayer.setAge(player.getAge());
        String position = player.getPosition();
        newPlayer.setPosition(Position.valueOf(position.substring(0, 1).toUpperCase() + position.substring(1).toLowerCase()));
        newPlayer.setMarketValue(player.getMarketValue());
        newPlayer.setTeam(team);

        Player savedPlayer = playerRepository.save(newPlayer);
        return mapper.getPlayerDtoFromEntity(savedPlayer, true, false);
    }

    @Override
    @Transactional
    public PlayerDto getById(long id, Long teamId) throws ApplicationException {
        Player existPlayer = checkPlayerWithTeamId(id, teamId);

        return mapper.getPlayerDtoFromEntity(existPlayer, true, true);
    }

    @Override
    public PagingDataRespDto<PlayerDto> getAll(PagingDataReqDto pageRequest, Long teamId) throws ApplicationException {
        int pageSize = pageRequest.getPageSize();
        int pageNumber = pageRequest.getPageNumber();

        Page<Player> playerPage = null;
        try {
            if (teamId != null) {
                playerPage = playerRepository.findAllPlayersByTeamId(teamId,
                        PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "position")
                );
            } else {
                playerPage = playerRepository.findAllPlayers(
                        PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "position")
                );
            }
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationError.FILTER_CRITERIA_ERROR, ex.getMessage());
        }

        return new PagingDataRespDto<>(
                playerPage.getTotalElements(),
                playerPage.getTotalPages(),
                playerPage.get().map(p -> mapper.getPlayerDtoFromEntity(p, true, true))
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public List<PlayerDto> getAll(Long teamId) {
        List<Player> players;

        if (teamId != null) {
            players = playerRepository.findAllPlayersByTeamId(teamId);
        } else {
            players = playerRepository.findAllPlayers();
        }
        return players.stream().map(p -> mapper.getPlayerDtoFromEntity(p, true, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlayerDto update(long id, PlayerUpdateDto player, Long teamId) throws ApplicationException {
        Player existPlayer = checkPlayerWithTeamId(id, teamId);

        if (player.getFirstName() != null && !player.getFirstName().isBlank()) {
            existPlayer.setFirstName(player.getFirstName());
        }
        if (player.getLastName() != null && !player.getLastName().isBlank()) {
            existPlayer.setLastName(player.getLastName());
        }
        if (player.getCountry() != null && !player.getCountry().isBlank()) {
            existPlayer.setCountry(player.getCountry());
        }

        Player savedPlayer = playerRepository.save(existPlayer);
        return mapper.getPlayerDtoFromEntity(savedPlayer, true, true);
    }

    @Override
    @Transactional
    public boolean delete(long id, Long teamId) throws ApplicationException {
        Player deletedPlayer = checkPlayerWithTeamId(id, teamId);

        playerRepository.delete(deletedPlayer);
        //return mapper.getPlayerDtoFromEntity(deletedPlayer, true, true);
        return true;
    }

    private Player checkPlayerWithTeamId(long playerId, Long teamId) throws ApplicationException {
        Player existPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.PLAYER_NOT_FOUND));

        if (teamId != null && (existPlayer.getTeam() == null || existPlayer.getTeam().getId() != teamId)) {
            throw new ApplicationException(ApplicationError.UNAUTHORIZED_USER_ERROR);
        }

        return existPlayer;
    }
}
