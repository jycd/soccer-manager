package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;

import java.util.List;

public interface UserService {
    UserDto create(UserCreateDto user) throws ApplicationException;
    UserDto getById(long id) throws ApplicationException;
    PagingDataRespDto<UserDto> getAll(PagingDataReqDto pageRequest) throws ApplicationException;
    List<UserDto> getAll();
    UserDto update(long id, UserUpdateDto user) throws ApplicationException;
    boolean delete(long id) throws ApplicationException;
    UserDto login(CredentialDto credential) throws ApplicationException;
}
