package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Role;
import com.toptal.soccermanager.model.entity.Status;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.model.entity.User;
import com.toptal.soccermanager.repository.TeamRepository;
import com.toptal.soccermanager.repository.UserRepository;
import com.toptal.soccermanager.service.impl.TeamServiceImpl;
import com.toptal.soccermanager.utils.Mapper;
import com.toptal.soccermanager.utils.TeamPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TeamServiceImplTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private final long ID = 1L;
    private final String EMAIL = "abc@xyz.c";
    private final String FULL_NAME = "Test Name";
    private final String ENCODED_PASSWORD = "encodedtest";
    private final String ROLE = "ROLE_USER";
    private final String STATUS = "ACTIVE";
    private final String TEAM_NAME = "Test Name";
    private final String TEAM_COUNTRY = "Spain";
    private final double TEAM_BUDGET = 10000.0;
    private final double TEAM_MARKET_VALUE = 10000.0;

    private final User user = new User(ID, EMAIL, ENCODED_PASSWORD, FULL_NAME, Role.valueOf(ROLE), Status.valueOf(STATUS), 0, null);
    private final Team team = new Team(ID, TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET, user, new HashSet<>());
    private final TeamPair teamPair = new TeamPair(team, TEAM_MARKET_VALUE);
    private final TeamCreateDto createdTeam = new TeamCreateDto(TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET);
    private final TeamUpdateDto updatedTeam = new TeamUpdateDto(TEAM_NAME, TEAM_COUNTRY);
    private final TeamDto returnedTeam = new TeamDto(ID, TEAM_NAME, TEAM_COUNTRY, String.valueOf(TEAM_MARKET_VALUE), String.valueOf(TEAM_BUDGET), null, new HashSet<>());
    private final PagingDataReqDto pageRequest = new PagingDataReqDto(0, 50);

    @Test
    public void testCreate() throws ApplicationException {
        when(teamRepository.findById(ID)).thenReturn(Optional.empty());
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(mapper.getTeamDtoFromEntity(team, true, false, true)).thenReturn(returnedTeam);

        TeamDto actualTeam = teamService.create(createdTeam, ID);
        verifyTeamDto(returnedTeam, actualTeam);

        ArgumentCaptor<Team> argumentCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(argumentCaptor.capture());
        Team capturedTeam = argumentCaptor.getValue();
        verifyTeam(team, capturedTeam);
        assertEquals(user, capturedTeam.getUser());
    }

    @Test
    public void testCreate_moreThanOneTeam() {
        when(teamRepository.findById(ID)).thenReturn(Optional.of(team));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> teamService.create(createdTeam, ID));
        assertEquals(ApplicationError.MORE_THAN_ONE_TEAM, ex.getApplicationError());
    }

    @Test
    public void testCreate_userNotFound() {
        when(teamRepository.findById(ID)).thenReturn(Optional.empty());
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> teamService.create(createdTeam, ID));
        assertEquals(ApplicationError.USER_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testGetByIdWithPlayers() throws ApplicationException {
        when(teamRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(team));
        when(mapper.getTeamDtoFromEntity(team, true, false, true)).thenReturn(returnedTeam);

        TeamDto actualTeam = teamService.getByIdWithPlayers(ID);
        verifyTeamDto(returnedTeam, actualTeam);
    }

    @Test
    public void testGetByIdWithPlayers_teamNotFound() {
        when(teamRepository.findByIdWithPlayers(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> teamService.getByIdWithPlayers(ID));
        assertEquals(ApplicationError.TEAM_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testGetByIdWithoutPlayers() throws ApplicationException {
        when(teamRepository.findByIdWithoutPlayers(ID)).thenReturn(Optional.of(teamPair));
        when(mapper.getTeamDtoFromEntity(teamPair.getTeam(), teamPair.getMarketValue())).thenReturn(returnedTeam);

        TeamDto actualTeam = teamService.getByIdWithoutPlayers(ID);
        verifyTeamDto(returnedTeam, actualTeam);
    }

    @Test
    public void testGetByIdWithoutPlayers_teamNotFound() {
        when(teamRepository.findByIdWithoutPlayers(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> teamService.getByIdWithoutPlayers(ID));
        assertEquals(ApplicationError.TEAM_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testGetAllWithPagination() throws ApplicationException {
        Team dummyTeam = new Team(2L, TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET, user, new HashSet<>());
        TeamDto dummyTeamDto = new TeamDto(2L, TEAM_NAME, TEAM_COUNTRY, String.valueOf(TEAM_MARKET_VALUE), String.valueOf(TEAM_BUDGET), null, new HashSet<>());
        when(teamRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Arrays.asList(team, dummyTeam)));
        when(mapper.getTeamDtoFromEntity(team, false, false, false)).thenReturn(returnedTeam);
        when(mapper.getTeamDtoFromEntity(dummyTeam, false, false, false)).thenReturn(dummyTeamDto);

        PagingDataRespDto<TeamDto> pageResult = teamService.getAll(pageRequest);
        assertEquals(2, pageResult.getTotalElements());
        assertEquals(1, pageResult.getTotalPage());
        assertEquals(2, pageResult.getData().size());
        verifyTeamDto(returnedTeam, pageResult.getData().get(0));
        verifyTeamDto(dummyTeamDto, pageResult.getData().get(1));

        ArgumentCaptor<PageRequest> argumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(teamRepository).findAll(argumentCaptor.capture());
        PageRequest capturedRequest = argumentCaptor.getValue();
        assertEquals(0, capturedRequest.getPageNumber());
        assertEquals(50, capturedRequest.getPageSize());
    }

    @Test
    public void testGetAllWithPagination_Empty() throws ApplicationException {
        when(teamRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(new ArrayList<>()));
        PagingDataRespDto<TeamDto> pageResult = teamService.getAll(pageRequest);
        assertEquals(0, pageResult.getTotalElements());
        assertEquals(1, pageResult.getTotalPage());
        assertEquals(0, pageResult.getData().size());
    }

    @Test
    public void testGetAllWithoutPagination() {
        Team dummyTeam = new Team(2L, TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET, user, new HashSet<>());
        TeamDto dummyTeamDto = new TeamDto(2L, TEAM_NAME, TEAM_COUNTRY, String.valueOf(TEAM_MARKET_VALUE), String.valueOf(TEAM_BUDGET), null, new HashSet<>());
        when(teamRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(team, dummyTeam)));
        when(mapper.getTeamDtoFromEntity(team, false, false, false)).thenReturn(returnedTeam);
        when(mapper.getTeamDtoFromEntity(dummyTeam, false, false, false)).thenReturn(dummyTeamDto);

        List<TeamDto> actualTeams = teamService.getAll();
        assertEquals(2, actualTeams.size());
        verifyTeamDto(returnedTeam, actualTeams.get(0));
        verifyTeamDto(dummyTeamDto, actualTeams.get(1));
    }

    @Test
    public void testGetAllWithoutPagination_Empty() {
        when(teamRepository.findAll()).thenReturn(new ArrayList<>());

        List<TeamDto> actualTeams = teamService.getAll();
        assertEquals(0, actualTeams.size());
    }

    @Test
    public void testUpdate() throws ApplicationException {
        Team dummyTeam = new Team(2L, "Another Team", "Italy", TEAM_BUDGET, user, new HashSet<>());
        TeamPair dummyTeamPair = new TeamPair(dummyTeam, TEAM_MARKET_VALUE);
        when(teamRepository.findByIdWithoutPlayers(ID)).thenReturn(Optional.of(dummyTeamPair));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(mapper.getTeamDtoFromEntity(team, dummyTeamPair.getMarketValue())).thenReturn(returnedTeam);

        teamService.update(ID, updatedTeam);

        ArgumentCaptor<Team> argumentCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(argumentCaptor.capture());
        Team capturedTeam = argumentCaptor.getValue();
        verifyTeam(team, capturedTeam);
    }

    @Test
    public void testUpdate_teamNotFound() {
        when(teamRepository.findByIdWithoutPlayers(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> teamService.update(ID, updatedTeam));
        assertEquals(ApplicationError.TEAM_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testDelete() throws ApplicationException {
        when(teamRepository.findById(ID)).thenReturn(Optional.of(team));
        doNothing().when(teamRepository).delete(team);

        boolean success = teamService.delete(ID);
        assertTrue(success);
    }

    @Test
    public void testDelete_teamNotFound() {
        when(teamRepository.findById(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> teamService.delete(ID));
        assertEquals(ApplicationError.TEAM_NOT_FOUND, ex.getApplicationError());
    }

    private void verifyTeamDto(TeamDto expected, TeamDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getMarketValue(), actual.getMarketValue());
        assertEquals(expected.getBudget(), actual.getBudget());
    }

    private void verifyTeam(Team expected, Team actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getBudget(), actual.getBudget());
    }
}
