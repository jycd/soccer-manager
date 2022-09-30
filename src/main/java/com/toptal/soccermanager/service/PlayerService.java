package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;

import java.util.List;

public interface PlayerService {
    PlayerDto create(PlayerCreateDto player, long teamId) throws ApplicationException;
    PlayerDto getById(long id, Long teamId) throws ApplicationException;
    PagingDataRespDto<PlayerDto> getAll(PagingDataReqDto pageRequest, Long teamId) throws ApplicationException;
    List<PlayerDto> getAll(Long teamId);
    PlayerDto update(long id, PlayerUpdateDto player, Long teamId) throws ApplicationException;
    boolean delete(long id, Long teamId) throws ApplicationException;
}
