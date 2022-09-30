package com.toptal.soccermanager.service;

import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Player;
import com.toptal.soccermanager.model.entity.Position;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.model.entity.Transfer;
import com.toptal.soccermanager.repository.PlayerRepository;
import com.toptal.soccermanager.repository.TeamRepository;
import com.toptal.soccermanager.repository.TransferRepository;
import com.toptal.soccermanager.service.impl.TransferServiceImpl;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TransferServiceImplTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private Creator creator;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private final long ID = 1L;
    private final String FIRST_NAME = "First Name";
    private final String LAST_NAME = "Last Name";
    private final String PLAYER_COUNTRY = "Spain";
    private final int AGE = 25;
    private final String POSITION = "Attacker";
    private final double PLAYER_MARKET_VALUE = 10000.0;
    private final double ASK_PRICE = 20000.0;
    private final long TEAM_ID = 2L;
    private final String TEAM_NAME = "Test Name";
    private final String TEAM_COUNTRY = "Spain";
    private final double TEAM_BUDGET = 100000.0;

    private final Team team = new Team(TEAM_ID, TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET, null, new HashSet<>());
    private final Team anotherTeam = new Team(3L, TEAM_NAME, TEAM_COUNTRY, TEAM_BUDGET, null, new HashSet<>());
    private final Player player = new Player(ID, FIRST_NAME, LAST_NAME, PLAYER_COUNTRY, AGE, Position.valueOf(POSITION), PLAYER_MARKET_VALUE, team, null);
    private final Transfer transfer = new Transfer(ID, ASK_PRICE, player);
    private final TransferCreateDto createdTransfer = new TransferCreateDto(ID, ASK_PRICE);
    private final TransferUpdateDto updatedTransfer = new TransferUpdateDto(ASK_PRICE);
    private final TransferDto returnedTransfer = new TransferDto(ID, String.valueOf(ASK_PRICE), null);
    private final PagingDataReqDto pageRequest = new PagingDataReqDto(0, 50);

    @Test
    public void testCreate() throws ApplicationException {
        when(playerRepository.findById(ID)).thenReturn(Optional.of(player));
        when(transferRepository.findById(ID)).thenReturn(Optional.empty());
        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
        when(mapper.getTransferDtoFromEntity(transfer, true)).thenReturn(returnedTransfer);

        TransferDto actualTransfer = transferService.create(createdTransfer, TEAM_ID);
        verifyTransferDto(returnedTransfer, actualTransfer);

        ArgumentCaptor<Transfer> argumentCaptor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(argumentCaptor.capture());
        Transfer capturedTransfer = argumentCaptor.getValue();
        verifyTransfer(transfer, capturedTransfer);
        assertEquals(player, capturedTransfer.getPlayer());
    }

    @Test
    public void testCreate_playerNotFound() {
        when(playerRepository.findById(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> transferService.create(createdTransfer, TEAM_ID));
        assertEquals(ApplicationError.PLAYER_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testCreate_unAuthorized() {
        when(playerRepository.findById(ID)).thenReturn(Optional.of(player));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> transferService.create(createdTransfer, 3L));
        assertEquals(ApplicationError.UNAUTHORIZED_USER_ERROR, ex.getApplicationError());
    }

    @Test
    public void testCreate_transferExists() {
        when(playerRepository.findById(ID)).thenReturn(Optional.of(player));
        when(transferRepository.findById(ID)).thenReturn(Optional.of(transfer));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> transferService.create(createdTransfer, null));
        assertEquals(ApplicationError.TRANSFER_DUPLICATED, ex.getApplicationError());
    }

    @Test
    public void testGetById() throws ApplicationException {
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(transfer));
        when(mapper.getTransferDtoFromEntity(transfer, true)).thenReturn(returnedTransfer);

        TransferDto actualTransfer = transferService.getById(ID);
        verifyTransferDto(returnedTransfer, actualTransfer);
    }

    @Test
    public void testGetById_transferNotFound() {
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> transferService.getById(ID));
        assertEquals(ApplicationError.TRANSFER_NOT_FOUND, ex.getApplicationError());
    }

    @Test
    public void testGetAllWithPagination() throws ApplicationException {
        Transfer dummyTransfer = new Transfer(2L, ASK_PRICE, player);
        TransferDto dummyTransferDto = new TransferDto(2L, String.valueOf(ASK_PRICE), null);
        when(transferRepository.findAllWithPlayers(any(PageRequest.class))).thenReturn(new PageImpl<>(Arrays.asList(transfer, dummyTransfer)));
        when(mapper.getTransferDtoFromEntity(transfer, true)).thenReturn(returnedTransfer);
        when(mapper.getTransferDtoFromEntity(dummyTransfer, true)).thenReturn(dummyTransferDto);

        PagingDataRespDto<TransferDto> pageResult = transferService.getAll(pageRequest);
        assertEquals(2, pageResult.getTotalElements());
        assertEquals(1, pageResult.getTotalPage());
        assertEquals(2, pageResult.getData().size());
        verifyTransferDto(returnedTransfer, pageResult.getData().get(0));
        verifyTransferDto(dummyTransferDto, pageResult.getData().get(1));

        ArgumentCaptor<PageRequest> argumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(transferRepository).findAllWithPlayers(argumentCaptor.capture());
        PageRequest capturedRequest = argumentCaptor.getValue();
        assertEquals(0, capturedRequest.getPageNumber());
        assertEquals(50, capturedRequest.getPageSize());
    }

    @Test
    public void testGetAllWithoutPagination() {
        Transfer dummyTransfer = new Transfer(2L, ASK_PRICE, player);
        TransferDto dummyTransferDto = new TransferDto(2L, String.valueOf(ASK_PRICE), null);
        when(transferRepository.findAllWithPlayers()).thenReturn(new ArrayList<>(Arrays.asList(transfer, dummyTransfer)));
        when(mapper.getTransferDtoFromEntity(transfer, true)).thenReturn(returnedTransfer);
        when(mapper.getTransferDtoFromEntity(dummyTransfer, true)).thenReturn(dummyTransferDto);

        List<TransferDto> actualTransfers = transferService.getAll();
        assertEquals(2, actualTransfers.size());
        verifyTransferDto(returnedTransfer, actualTransfers.get(0));
        verifyTransferDto(dummyTransferDto, actualTransfers.get(1));
    }

    @Test
    public void testUpdate() throws ApplicationException {
        Transfer dummyTransfer = new Transfer(ID, 1213213.0, player);
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(dummyTransfer));
        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
        when(mapper.getTransferDtoFromEntity(transfer, true)).thenReturn(returnedTransfer);

        transferService.update(ID, updatedTransfer, TEAM_ID);

        ArgumentCaptor<Transfer> argumentCaptor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(argumentCaptor.capture());
        Transfer capturedTransfer = argumentCaptor.getValue();
        verifyTransfer(transfer, capturedTransfer);
    }

    @Test
    public void testDelete_withdrawTransfer() throws ApplicationException {
        player.setTransfer(transfer);
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(transfer));
        doNothing().when(transferRepository).delete(transfer);

        boolean success = transferService.delete(ID, TEAM_ID);
        assertTrue(success);
        assertNull(player.getTransfer());
        assertEquals(TEAM_BUDGET, team.getBudget());
        assertEquals(PLAYER_MARKET_VALUE, player.getMarketValue());
    }

    @Test
    public void testDelete_withdrawTransfer_nullTeamId() throws ApplicationException {
        player.setTransfer(transfer);
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(transfer));
        doNothing().when(transferRepository).delete(transfer);

        boolean success = transferService.delete(ID, null);
        assertTrue(success);
        assertNull(player.getTransfer());
        assertEquals(TEAM_BUDGET, team.getBudget());
        assertEquals(PLAYER_MARKET_VALUE, player.getMarketValue());
    }

    @Test
    public void testDelete_buyPlayer() throws ApplicationException {
        player.setTransfer(transfer);
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(transfer));
        when(teamRepository.findById(anotherTeam.getId())).thenReturn(Optional.of(anotherTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(new Team());
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        doNothing().when(transferRepository).delete(transfer);

        boolean success = transferService.delete(ID, anotherTeam.getId());
        assertTrue(success);
        assertNull(player.getTransfer());
        assertEquals(TEAM_BUDGET + ASK_PRICE, team.getBudget());
        assertEquals(TEAM_BUDGET - ASK_PRICE, anotherTeam.getBudget());
        assertEquals(anotherTeam, player.getTeam());
    }

    @Test
    public void testDelete_teamNotFound() {
        player.setTransfer(transfer);
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(transfer));
        when(teamRepository.findById(anotherTeam.getId())).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> transferService.delete(ID, anotherTeam.getId()));
        assertEquals(ApplicationError.TEAM_NOT_FOUND, ex.getApplicationError());
        assertEquals(TEAM_BUDGET, team.getBudget());
        assertEquals(TEAM_BUDGET, anotherTeam.getBudget());
        assertEquals(PLAYER_MARKET_VALUE, player.getMarketValue());
        assertEquals(transfer, player.getTransfer());
    }

    @Test
    public void testDelete_inSufficientBudget() {
        transfer.setAskPrice(TEAM_BUDGET + 1);
        player.setTransfer(transfer);
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(transfer));
        when(teamRepository.findById(anotherTeam.getId())).thenReturn(Optional.of(anotherTeam));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> transferService.delete(ID, anotherTeam.getId()));
        assertEquals(ApplicationError.TEAM_INSUFFICIENT_BUDGET, ex.getApplicationError());
        assertEquals(TEAM_BUDGET, team.getBudget());
        assertEquals(TEAM_BUDGET, anotherTeam.getBudget());
        assertEquals(PLAYER_MARKET_VALUE, player.getMarketValue());
        assertEquals(transfer, player.getTransfer());
    }

    @Test
    public void testUpdate_unAuthorized() {
        when(transferRepository.findByIdWithPlayers(ID)).thenReturn(Optional.of(transfer));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> transferService.update(ID, updatedTransfer, 3L));
        assertEquals(ApplicationError.UNAUTHORIZED_USER_ERROR, ex.getApplicationError());
    }

    private void verifyTransferDto(TransferDto expected, TransferDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAskPrice(), actual.getAskPrice());
    }

    private void verifyTransfer(Transfer expected, Transfer actual) {
        assertEquals(expected.getAskPrice(), actual.getAskPrice());
    }
}
