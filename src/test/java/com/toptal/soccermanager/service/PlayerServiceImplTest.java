package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Player;
import com.toptal.soccermanager.model.entity.Position;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.repository.PlayerRepository;
import com.toptal.soccermanager.repository.TeamRepository;
import com.toptal.soccermanager.service.impl.PlayerServiceImpl;
import com.toptal.soccermanager.utils.Mapper;
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
public class PlayerServiceImplTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private final long ID = 1L;
    private final String FIRST_NAME = "First Name";
    private final String LAST_NAME = "Last Name";
    private final String PLAYER_COUNTRY = "Spain";
    private final int AGE = 25;
    private final String POSITION = "Attacker";
    private final double PLAYER_MARKET_VALUE = 10000.0;
    private final long TEAM_ID = 1L;
    private final String TEAM_NAME = "Test Name";
    private final String TEAM_COUNTRY = "Spain";
    private final double TEAM_BUDGET = 10000.0;

    private final Team team = new Team(TEAM_ID, TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET, null, new HashSet<>());
    private final Player player = new Player(ID, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, Position.valueOf(POSITION), PLAYER_MARKET_VALUE, team, null);
    private final PlayerCreateDto createdPlayer = new PlayerCreateDto(FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, POSITION, PLAYER_MARKET_VALUE);
    private final PlayerUpdateDto updatedPlayer = new PlayerUpdateDto(FIRST_NAME, LAST_NAME, PLAYER_COUNTRY);
    private final PlayerDto returnedPlayer = new PlayerDto(ID, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, POSITION, String.valueOf(PLAYER_MARKET_VALUE), null, null);
    private final PagingDataReqDto pageRequest = new PagingDataReqDto(0, 50);

    @Test
    public void testCreate() throws ApplicationException {
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(mapper.getPlayerDtoFromEntity(player, true, false)).thenReturn(returnedPlayer);

        PlayerDto actualPlayer = playerService.create(createdPlayer, TEAM_ID);
        verifyPlayerDto(returnedPlayer, actualPlayer);

        ArgumentCaptor<Player> argumentCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(argumentCaptor.capture());
        Player capturedPlayer = argumentCaptor.getValue();
        verifyPlayer(player, capturedPlayer);
        assertEquals(team, capturedPlayer.getTeam());
    }

    @Test
    public void testCreate_teamNotFound() {
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> playerService.create(createdPlayer, TEAM_ID));
        assertEquals(ApplicationError.TEAM_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testGetById() throws ApplicationException {
        when(playerRepository.findById(ID)).thenReturn(Optional.of(player));
        when(mapper.getPlayerDtoFromEntity(player, true, true)).thenReturn(returnedPlayer);

        PlayerDto actualPlayer = playerService.getById(ID, TEAM_ID);
        verifyPlayerDto(returnedPlayer, actualPlayer);
    }

    @Test
    public void testCheckPlayerWithTeamId_notPlayerFound() {
        when(playerRepository.findById(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> playerService.getById(ID, TEAM_ID));
        assertEquals(ApplicationError.PLAYER_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testCheckPlayerWithTeamId_unAuthorized() {
        when(playerRepository.findById(ID)).thenReturn(Optional.of(player));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> playerService.getById(ID, 2L));
        assertEquals(ApplicationError.UNAUTHORIZED_USER_ERROR, ex.getApplicationError());
    }

    @Test
    public void testGetAllWithPagination_withTeamId() throws ApplicationException {
        Player dummyPlayer = new Player(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, Position.valueOf(POSITION), PLAYER_MARKET_VALUE, team, null);
        PlayerDto dummyPlayerDto = new PlayerDto(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, POSITION, String.valueOf(PLAYER_MARKET_VALUE), null, null);
        when(playerRepository.findAllPlayersByTeamId(eq(TEAM_ID), any(PageRequest.class))).thenReturn(new PageImpl<>(Arrays.asList(player, dummyPlayer)));
        when(mapper.getPlayerDtoFromEntity(player, true, true)).thenReturn(returnedPlayer);
        when(mapper.getPlayerDtoFromEntity(dummyPlayer, true, true)).thenReturn(dummyPlayerDto);

        PagingDataRespDto<PlayerDto> pageResult = playerService.getAll(pageRequest, TEAM_ID);
        assertEquals(2, pageResult.getTotalElements());
        assertEquals(1, pageResult.getTotalPage());
        assertEquals(2, pageResult.getData().size());
        verifyPlayerDto(returnedPlayer, pageResult.getData().get(0));
        verifyPlayerDto(dummyPlayerDto, pageResult.getData().get(1));

        ArgumentCaptor<PageRequest> argumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(playerRepository).findAllPlayersByTeamId(eq(TEAM_ID), argumentCaptor.capture());
        PageRequest capturedRequest = argumentCaptor.getValue();
        assertEquals(0, capturedRequest.getPageNumber());
        assertEquals(50, capturedRequest.getPageSize());
    }

    @Test
    public void testGetAllWithPagination_withoutTeamId() throws ApplicationException {
        Player dummyPlayer = new Player(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, Position.valueOf(POSITION), PLAYER_MARKET_VALUE, team, null);
        PlayerDto dummyPlayerDto = new PlayerDto(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, POSITION, String.valueOf(PLAYER_MARKET_VALUE), null, null);
        when(playerRepository.findAllPlayers(any(PageRequest.class))).thenReturn(new PageImpl<>(Arrays.asList(player, dummyPlayer)));
        when(mapper.getPlayerDtoFromEntity(player, true, true)).thenReturn(returnedPlayer);
        when(mapper.getPlayerDtoFromEntity(dummyPlayer, true, true)).thenReturn(dummyPlayerDto);

        PagingDataRespDto<PlayerDto> pageResult = playerService.getAll(pageRequest, null);
        assertEquals(2, pageResult.getTotalElements());
        assertEquals(1, pageResult.getTotalPage());
        assertEquals(2, pageResult.getData().size());
    }

    @Test
    public void testGetAllWithoutPagination_withTeamId() {
        Player dummyPlayer = new Player(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, Position.valueOf(POSITION), PLAYER_MARKET_VALUE, team, null);
        PlayerDto dummyPlayerDto = new PlayerDto(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, POSITION, String.valueOf(PLAYER_MARKET_VALUE), null, null);
        when(playerRepository.findAllPlayersByTeamId(TEAM_ID)).thenReturn(new ArrayList<>(Arrays.asList(player, dummyPlayer)));
        when(mapper.getPlayerDtoFromEntity(player, true, true)).thenReturn(returnedPlayer);
        when(mapper.getPlayerDtoFromEntity(dummyPlayer, true, true)).thenReturn(dummyPlayerDto);

        List<PlayerDto> actualPlayers = playerService.getAll(TEAM_ID);
        assertEquals(2, actualPlayers.size());
    }

    @Test
    public void testGetAllWithoutPagination_withoutTeamId() {
        Player dummyPlayer = new Player(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, Position.valueOf(POSITION), PLAYER_MARKET_VALUE, team, null);
        PlayerDto dummyPlayerDto = new PlayerDto(2L, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, POSITION, String.valueOf(PLAYER_MARKET_VALUE), null, null);
        when(playerRepository.findAllPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player, dummyPlayer)));
        when(mapper.getPlayerDtoFromEntity(player, true, true)).thenReturn(returnedPlayer);
        when(mapper.getPlayerDtoFromEntity(dummyPlayer, true, true)).thenReturn(dummyPlayerDto);

        List<PlayerDto> actualPlayers = playerService.getAll(null);
        assertEquals(2, actualPlayers.size());
    }

    @Test
    public void testUpdate() throws ApplicationException {
        Player dummyPlayer = new Player(ID, "Another First", "Another Last", "Italy", AGE, Position.valueOf(POSITION), PLAYER_MARKET_VALUE, team, null);
        when(playerRepository.findById(ID)).thenReturn(Optional.of(dummyPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(mapper.getPlayerDtoFromEntity(player, true, true)).thenReturn(returnedPlayer);

        playerService.update(ID, updatedPlayer, null);

        ArgumentCaptor<Player> argumentCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(argumentCaptor.capture());
        Player capturedPlayer = argumentCaptor.getValue();
        verifyPlayer(player, capturedPlayer);
    }

    @Test
    public void testDelete() throws ApplicationException {
        when(playerRepository.findById(ID)).thenReturn(Optional.of(player));
        doNothing().when(playerRepository).delete(player);

        boolean success = playerService.delete(ID, TEAM_ID);
        assertTrue(success);
    }

    private void verifyPlayerDto(PlayerDto expected, PlayerDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertEquals(expected.getMarketValue(), actual.getMarketValue());
    }

    private void verifyPlayer(Player expected, Player actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertEquals(expected.getMarketValue(), actual.getMarketValue());
    }
}
