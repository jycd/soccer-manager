package com.toptal.soccermanager.service.impl;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.model.entity.User;
import com.toptal.soccermanager.repository.TeamRepository;
import com.toptal.soccermanager.repository.UserRepository;
import com.toptal.soccermanager.service.TeamService;
import com.toptal.soccermanager.utils.Mapper;
import com.toptal.soccermanager.utils.TeamPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Mapper mapper;

    @Override
    @Transactional
    public TeamDto create(TeamCreateDto team, long userId) throws ApplicationException {
        if (teamRepository.findById(userId).isPresent()) {
            throw new ApplicationException(ApplicationError.MORE_THAN_ONE_TEAM);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.USER_NOT_FOUND));

        Team newTeam = new Team();
        newTeam.setName(team.getName());
        newTeam.setCountry(team.getCountry());
        newTeam.setBudget(team.getBudget());
        newTeam.setUser(user);

        Team savedTeam = teamRepository.save(newTeam);
        return mapper.getTeamDtoFromEntity(savedTeam, true, false, true);
    }

    @Override
    @Transactional
    public TeamDto getByIdWithPlayers(long id) throws ApplicationException {
        Team existTeam = teamRepository.findByIdWithPlayers(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        return mapper.getTeamDtoFromEntity(existTeam, true, false, true);
    }

    @Override
    @Transactional
    public TeamDto getByIdWithoutPlayers(long id) throws ApplicationException {
        TeamPair existTeam = teamRepository.findByIdWithoutPlayers(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        return mapper.getTeamDtoFromEntity(existTeam.getTeam(), existTeam.getMarketValue());
    }

    @Override
    public PagingDataRespDto<TeamDto> getAll(PagingDataReqDto pageRequest) throws ApplicationException {
        int pageSize = pageRequest.getPageSize();
        int pageNumber = pageRequest.getPageNumber();

        Page<Team> teamPage = null;
        try {
            teamPage = teamRepository.findAll(
                    PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "name")
            );
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationError.FILTER_CRITERIA_ERROR, ex.getMessage());
        }

        return new PagingDataRespDto<>(
                teamPage.getTotalElements(),
                teamPage.getTotalPages(),
                teamPage.get().map(t -> mapper.getTeamDtoFromEntity(t, false, false, false)).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public List<TeamDto> getAll() {
        List<Team> existTeams = teamRepository.findAll();
        return existTeams.stream().map(t -> mapper.getTeamDtoFromEntity(t, false, false, false)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamDto update(long id, TeamUpdateDto team) throws ApplicationException {
        TeamPair teamPair = teamRepository.findByIdWithoutPlayers(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        Team existTeam  = teamPair.getTeam();
        if (team.getName() != null && !team.getName().isBlank()) {
            existTeam.setName(team.getName());
        }
        if (team.getCountry() != null && !team.getCountry().isBlank()) {
            existTeam.setCountry(team.getCountry());
        }

        Team updatedTeam = teamRepository.save(existTeam);
        return mapper.getTeamDtoFromEntity(updatedTeam, teamPair.getMarketValue());
    }

    @Override
    @Transactional
    public boolean delete(long id) throws ApplicationException {
        Team existTeam = teamRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
        teamRepository.delete(existTeam);
        //return mapper.getTeamDtoFromEntity(existTeam, false, false,false);
        return true;
    }
}
