package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Role;
import com.toptal.soccermanager.model.entity.Status;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.model.entity.User;
import com.toptal.soccermanager.repository.UserRepository;
import com.toptal.soccermanager.service.impl.UserServiceImpl;
import com.toptal.soccermanager.utils.Creator;
import com.toptal.soccermanager.utils.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Creator creator;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final long ID = 1L;
    private final String EMAIL = "abc@xyz.c";
    private final String FULL_NAME = "Test Name";
    private final String PASSWORD = "test";
    private final String ENCODED_PASSWORD = "encodedtest";
    private final String ROLE = "ROLE_USER";
    private final String STATUS = "ACTIVE";
    private final String TEAM_NAME = "Test Name";
    private final String TEAM_COUNTRY = "Spain";
    private final double TEAM_BUDGET = 10000.0;

    private final CredentialDto credential = new CredentialDto(EMAIL, PASSWORD);
    private final UserCreateDto createdUser = new UserCreateDto(EMAIL, PASSWORD, FULL_NAME, ROLE);
    private final UserUpdateDto updatedUser = new UserUpdateDto(EMAIL, PASSWORD, FULL_NAME, ROLE, STATUS);
    private final Team team = new Team(ID, TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET, null, null);
    private final User user = new User(ID, EMAIL, ENCODED_PASSWORD, FULL_NAME, Role.valueOf(ROLE), Status.valueOf(STATUS), 0, team);
    private final UserDto returnedUser = new UserDto(ID, EMAIL, FULL_NAME, STATUS, ROLE);
    private final PagingDataReqDto pageRequest = new PagingDataReqDto(0, 50);

    @Test
    public void testCreate() throws ApplicationException {
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(creator.createTeam()).thenReturn(team);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.getUserDtoFromEntity(user)).thenReturn(returnedUser);

        UserDto actualUser = userService.create(createdUser);
        verifyUserDto(returnedUser, actualUser);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User capturedUser = argumentCaptor.getValue();
        verifyUser(user, capturedUser);
        assertEquals(team, capturedUser.getTeam());
        assertEquals(capturedUser, team.getUser());
    }

    @Test
    public void testCreate_emailExists() {
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(user));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.create(createdUser));
        assertEquals(ApplicationError.USER_DUPLICATED, ex.getApplicationError());
    }

    @Test
    public void testGetById() throws ApplicationException {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(mapper.getUserDtoFromEntity(user)).thenReturn(returnedUser);

        UserDto actualUser = userService.getById(ID);
        verifyUserDto(returnedUser, actualUser);
    }

    @Test
    public void testGetById_userNotFound() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.getById(ID));
        assertEquals(ApplicationError.USER_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testGetAllWithPagination() throws ApplicationException {
        User dummyUser = new User(2L, "abc@d.e", "pass", "Another User", Role.ROLE_USER, Status.ACTIVE, 0, null);
        UserDto dummyUserDto = new UserDto(2L, "abc@d.e", "Another User",  "ROLE_USER", "ACTIVE");
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Arrays.asList(user, dummyUser)));
        when(mapper.getUserDtoFromEntity(user)).thenReturn(returnedUser);
        when(mapper.getUserDtoFromEntity(dummyUser)).thenReturn(dummyUserDto);

        PagingDataRespDto<UserDto> pageResult = userService.getAll(pageRequest);
        assertEquals(2, pageResult.getTotalElements());
        assertEquals(1, pageResult.getTotalPage());
        assertEquals(2, pageResult.getData().size());
        verifyUserDto(returnedUser, pageResult.getData().get(0));
        verifyUserDto(dummyUserDto, pageResult.getData().get(1));

        ArgumentCaptor<PageRequest> argumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(userRepository).findAll(argumentCaptor.capture());
        PageRequest capturedRequest = argumentCaptor.getValue();
        assertEquals(0, capturedRequest.getPageNumber());
        assertEquals(50, capturedRequest.getPageSize());
    }

    @Test
    public void testGetAllWithPagination_Empty() throws ApplicationException {
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(new ArrayList<>()));
        PagingDataRespDto<UserDto> pageResult = userService.getAll(pageRequest);
        assertEquals(0, pageResult.getTotalElements());
        assertEquals(1, pageResult.getTotalPage());
        assertEquals(0, pageResult.getData().size());
    }

    @Test
    public void testGetAllWithoutPagination() {
        User dummyUser = new User(2L, "abc@d.e", "pass", "Another User", Role.ROLE_USER, Status.ACTIVE, 0, null);
        UserDto dummyUserDto = new UserDto(2L, "abc@d.e", "Another User", "ROLE_USER", "ACTIVE");
        when(userRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(user, dummyUser)));
        when(mapper.getUserDtoFromEntity(user)).thenReturn(returnedUser);
        when(mapper.getUserDtoFromEntity(dummyUser)).thenReturn(dummyUserDto);

        List<UserDto> actualUsers = userService.getAll();
        assertEquals(2, actualUsers.size());
        verifyUserDto(returnedUser, actualUsers.get(0));
        verifyUserDto(dummyUserDto, actualUsers.get(1));
    }

    @Test
    public void testGetAllWithoutPagination_Empty() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> actualUsers = userService.getAll();
        assertEquals(0, actualUsers.size());
    }

    @Test
    public void testUpdate() throws ApplicationException {
        User dummyUser = new User(ID, "abc@d.e", "pass", "Another User", Role.ROLE_USER, Status.BLOCKED, 2, null);
        when(userRepository.findById(ID)).thenReturn(Optional.of(dummyUser));
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.getUserDtoFromEntity(user)).thenReturn(returnedUser);

        userService.update(ID, updatedUser);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User capturedUser = argumentCaptor.getValue();
        verifyUser(user, capturedUser);
    }

    @Test
    public void testUpdate_userNotFound() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.update(ID, updatedUser));
        assertEquals(ApplicationError.USER_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testUpdate_emailExists() {
        User dummyUser = new User(ID, "abc@d.e", "pass", "Another User", Role.ROLE_USER, Status.BLOCKED, 2, null);
        when(userRepository.findById(ID)).thenReturn(Optional.of(dummyUser));
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(user));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.update(ID, updatedUser));
        assertEquals(ApplicationError.USER_DUPLICATED, ex.getApplicationError());
    }

    @Test
    public void testDelete() throws ApplicationException {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        boolean success = userService.delete(ID);
        assertTrue(success);
    }

    @Test
    public void testDelete_userNotFound() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.delete(ID));
        assertEquals(ApplicationError.USER_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testLogin() throws ApplicationException {
        user.setLoginAttempts(3);
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);
        when(mapper.getUserDtoFromEntity(any(User.class))).thenReturn(returnedUser);

        UserDto actualUser = userService.login(credential);
        verifyUserDto(returnedUser, actualUser);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mapper).getUserDtoFromEntity(argumentCaptor.capture());
        User capturedUser = argumentCaptor.getValue();
        assertEquals(0, capturedUser.getLoginAttempts());
    }

    @Test
    public void testLogin_credentialInvalid() {
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.login(credential));
        assertEquals(ApplicationError.USER_CREDENTIALS_INVALID, ex.getApplicationError());
    }

    @Test
    public void testLogin_userBlocked() {
        user.setStatus(Status.BLOCKED);
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(user));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.login(credential));
        assertEquals(ApplicationError.USER_BLOCKED, ex.getApplicationError());
    }

    @Test
    public void testLogin_invalidPassword_lessThanMaxLoginAttempts() {
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(false);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.login(credential));
        assertEquals(ApplicationError.USER_CREDENTIALS_INVALID, ex.getApplicationError());
        assertEquals("Remaining login attempts: 2", ex.getAdditionalMessage());

        assertEquals(1, user.getLoginAttempts());
    }

    @Test
    public void testLogin_invalidPassword_exceedMaxLoginAttempts() {
        user.setLoginAttempts(2);
        when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(false);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> userService.login(credential));
        assertEquals(ApplicationError.USER_CREDENTIALS_INVALID, ex.getApplicationError());
        assertEquals("Remaining login attempts: 0", ex.getAdditionalMessage());

        assertEquals(Status.BLOCKED, user.getStatus());
        assertEquals(0, user.getLoginAttempts());
    }

    private void verifyUserDto(UserDto expected, UserDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getRole(), actual.getRole());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    private void verifyUser(User expected, User actual) {
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getRole(), actual.getRole());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getLoginAttempts(), actual.getLoginAttempts());
    }
}
