package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;

import java.util.List;

public interface TeamService {
    TeamDto create(TeamCreateDto team, long userId) throws ApplicationException;
    TeamDto getByIdWithPlayers(long id) throws ApplicationException;
    TeamDto getByIdWithoutPlayers(long id) throws ApplicationException;
    PagingDataRespDto<TeamDto> getAll(PagingDataReqDto pageRequest) throws ApplicationException;
    List<TeamDto> getAll();
    TeamDto update(long id, TeamUpdateDto player) throws ApplicationException;
    boolean delete(long id) throws ApplicationException;
}
