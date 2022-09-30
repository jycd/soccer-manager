package com.toptal.soccermanager.service.impl;

import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Role;
import com.toptal.soccermanager.model.entity.Status;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.model.entity.User;
import com.toptal.soccermanager.repository.UserRepository;
import com.toptal.soccermanager.service.UserService;
import com.toptal.soccermanager.utils.Creator;
import com.toptal.soccermanager.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Creator creator;

    @Autowired
    private Mapper mapper;

    @Override
    @Transactional
    public UserDto create(UserCreateDto user) throws ApplicationException {
        Optional<User> optionalUser = userRepository.findOneByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new ApplicationException(ApplicationError.USER_DUPLICATED);
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setFullName(user.getFullName());
        newUser.setRole(Role.valueOf(user.getRole().toUpperCase()));
        newUser.setStatus((Status.ACTIVE));
        newUser.setLoginAttempts(0);
        Team createdTeam = creator.createTeam();
        newUser.setTeam(createdTeam);
        createdTeam.setUser(newUser);

        User savedUser = userRepository.save(newUser);
        return mapper.getUserDtoFromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserDto getById(long id) throws ApplicationException {
        User existUser = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.USER_NOT_FOUND));

        return mapper.getUserDtoFromEntity(existUser);
    }

    @Override
    public PagingDataRespDto<UserDto> getAll(PagingDataReqDto pageRequest) throws ApplicationException {
        int pageSize = pageRequest.getPageSize();
        int pageNumber = pageRequest.getPageNumber();

        Page<User> userPage = null;
        try {
            userPage = userRepository.findAll(
                    PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "email")
            );
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationError.FILTER_CRITERIA_ERROR, ex.getMessage());
        }

        return new PagingDataRespDto<>(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.get().map(mapper::getUserDtoFromEntity).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public List<UserDto> getAll() {
        List<User> existUsers = userRepository.findAll();

        return existUsers.stream().map(mapper::getUserDtoFromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(long id, UserUpdateDto user) throws ApplicationException {
        User existUser = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.USER_NOT_FOUND));

        if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().equals(existUser.getEmail())) {
            if (userRepository.findOneByEmail(user.getEmail()).isPresent()) {
                throw new ApplicationException(ApplicationError.USER_DUPLICATED);
            }
            existUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            existUser.setFullName(user.getFullName());
        }
        if (user.getRole() != null) {
            existUser.setRole(Role.valueOf(user.getRole().toUpperCase()));
        }

        if (user.getStatus() != null) {
            Status status = Status.valueOf(user.getStatus().toUpperCase());
            if (status == Status.ACTIVE) {
                existUser.setLoginAttempts(0);
            }
            existUser.setStatus(status);
        }

        User updatedUser = userRepository.save(existUser);
        return mapper.getUserDtoFromEntity(updatedUser);
    }

    @Override
    @Transactional
    public boolean delete(long id) throws ApplicationException {
        User deletedUser = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.USER_NOT_FOUND));

        userRepository.delete(deletedUser);
        //return mapper.getUserDtoFromEntity(deletedUser);
        return true;
    }

    @Override
    @Transactional
    public UserDto login(CredentialDto credential) throws ApplicationException {
        User user = userRepository.findOneByEmail(credential.getEmail())
                .orElseThrow(() -> new ApplicationException(ApplicationError.USER_CREDENTIALS_INVALID));

        if (user.getStatus() == Status.BLOCKED) {
            throw new ApplicationException(ApplicationError.USER_BLOCKED);
        }

        if (!passwordEncoder.matches(credential.getPassword(), user.getPassword())) {
            int loginAttempts = user.getLoginAttempts();
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS - 1) {
                user.setStatus(Status.BLOCKED);
                user.setLoginAttempts(0);
            } else {
                user.setLoginAttempts(loginAttempts + 1);
            }
            throw new ApplicationException(ApplicationError.USER_CREDENTIALS_INVALID,
                    String.format("Remaining login attempts: %d", MAX_LOGIN_ATTEMPTS - loginAttempts - 1));
        }

        user.setLoginAttempts(0);

        return mapper.getUserDtoFromEntity(user);
    }
}
