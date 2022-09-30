package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;

import java.util.List;

public interface TransferService {
    TransferDto create(TransferCreateDto transfer, Long teamId) throws ApplicationException;
    TransferDto getById(long id) throws ApplicationException;
    PagingDataRespDto<TransferDto> getAll(PagingDataReqDto pageRequest) throws ApplicationException;
    List<TransferDto> getAll();
    TransferDto update(long id, TransferUpdateDto transfer, Long teamId) throws ApplicationException;
    boolean delete(long id, Long teamId) throws ApplicationException;
}
