package com.toptal.soccermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.service.PlayerService;
import com.toptal.soccermanager.utils.WithMockPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TeamPlayerControllerTest {
    @MockBean
    private PlayerService playerService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testCreate() throws Exception {
        PlayerCreateDto createdPlayer = new PlayerCreateDto();
        createdPlayer.setFirstName("First Name");
        createdPlayer.setLastName("Last Name");
        createdPlayer.setCountry("Spain");
        createdPlayer.setAge(30);
        createdPlayer.setPosition("attacker");
        createdPlayer.setMarketValue(100000.0);
        PlayerDto returnedPlayer = new PlayerDto();

        when(playerService.create(createdPlayer, 1L)).thenReturn(returnedPlayer);

        mockMvc.perform(post("/teams/{teamId}/players", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createdPlayer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(returnedPlayer));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testCreate_invalidParameters() throws Exception {
        PlayerCreateDto createdPlayer = new PlayerCreateDto();
        createdPlayer.setFirstName(" ");
        createdPlayer.setLastName("");
        createdPlayer.setCountry("1234567890123456789012345678901234567890123456789012345678901");
        createdPlayer.setAge(41);
        createdPlayer.setPosition("coach");
        createdPlayer.setMarketValue(-100.0);
        PlayerDto returnedPlayer = new PlayerDto();

        when(playerService.create(createdPlayer, 1L)).thenReturn(returnedPlayer);

        mockMvc.perform(post("/teams/{teamId}/players", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createdPlayer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields", hasSize(6)));
    }

    @Test
    @WithMockPrincipal
    public void testCreate_unAuthorized() throws Exception {
        PlayerCreateDto createdPlayer = new PlayerCreateDto();
        createdPlayer.setFirstName("First Name");
        createdPlayer.setLastName("Last Name");
        createdPlayer.setCountry("Spain");
        createdPlayer.setAge(30);
        createdPlayer.setPosition("attacker");
        createdPlayer.setMarketValue(100000.0);
        PlayerDto returnedPlayer = new PlayerDto();

        when(playerService.create(createdPlayer, 1L)).thenReturn(returnedPlayer);

        mockMvc.perform(post("/teams/{teamId}/players", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createdPlayer)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockPrincipal
    public void testGetById() throws Exception {
        PlayerDto returnedPlayer = new PlayerDto();
        when(playerService.getById(2L, 1L)).thenReturn(returnedPlayer);

        mockMvc.perform(get("/teams/{teamId}/players/{id}", 1L, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(returnedPlayer));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithTeamIdWithPagination() throws Exception {
        PagingDataRespDto<PlayerDto> pageResult = new PagingDataRespDto<>();

        when(playerService.getAll(any(PagingDataReqDto.class), eq(1L))).thenReturn(pageResult);

        mockMvc.perform(get("/teams/{teamId}/players", 1L)
                        .queryParam("size", "2")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(playerService).getAll(eq(new PagingDataReqDto(0, 2)), eq(1L));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithTeamIdWithPagination_nullArguments() throws Exception {
        PagingDataRespDto<PlayerDto> pageResult = new PagingDataRespDto<>();
        when(playerService.getAll(any(PagingDataReqDto.class), eq(1L))).thenReturn(pageResult);

        mockMvc.perform(get("/teams/{teamId}/players", 1L)
                        .queryParam("size", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(playerService).getAll(eq(new PagingDataReqDto(0, 100)), eq(1L));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithTeamIdWithPagination_invalidParameters() throws Exception {
        mockMvc.perform(get("/teams/{teamId}/players", 1L)
                        .queryParam("size", "abc")
                        .queryParam("page", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithTeamIdWithoutPagination() throws Exception {
        List<PlayerDto> returnedPlayers = new ArrayList<>();
        when(playerService.getAll(1L)).thenReturn(returnedPlayers);

        mockMvc.perform(get("/teams/{teamId}/players", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithTeamId_forbidden() throws Exception {
        mockMvc.perform(get("/teams/{teamId}/players", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockPrincipal
    public void testUpdate() throws Exception {
        PlayerDto returnedPlayer = new PlayerDto();
        PlayerUpdateDto updatedPlayer = new PlayerUpdateDto();
        updatedPlayer.setFirstName("First Name");
        updatedPlayer.setLastName("Last Name");
        updatedPlayer.setCountry("Spain");

        when(playerService.update(2L, updatedPlayer, 1L)).thenReturn(returnedPlayer);

        mockMvc.perform(patch("/teams/{teamId}/players/{id}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedPlayer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(returnedPlayer));
    }

    @Test
    @WithMockPrincipal
    public void testUpdate_invalidParameters() throws Exception {
        PlayerDto returnedPlayer = new PlayerDto();
        PlayerUpdateDto updatedPlayer = new PlayerUpdateDto();
        updatedPlayer.setFirstName("1234567890123456789012345678901");
        updatedPlayer.setLastName("1234567890123456789012345678901");
        updatedPlayer.setCountry("1234567890123456789012345678901234567890123456789012345678901");

        when(playerService.update(2L, updatedPlayer, 1L)).thenReturn(returnedPlayer);

        mockMvc.perform(patch("/teams/{teamId}/players/{id}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedPlayer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields", hasSize(3)));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testDelete() throws Exception {
        when(playerService.delete(2L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/teams/{teamId}/players/{id}", 1L, 2L))
                .andExpect(status().isNoContent());
    }
}
