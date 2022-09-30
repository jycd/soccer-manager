package com.toptal.soccermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toptal.soccermanager.model.dto.PagingDataReqDto;
import com.toptal.soccermanager.model.dto.PagingDataRespDto;
import com.toptal.soccermanager.model.dto.TeamDto;
import com.toptal.soccermanager.model.dto.TeamUpdateDto;
import com.toptal.soccermanager.service.TeamService;
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
public class TeamControllerTest {
    @MockBean
    private TeamService teamService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockPrincipal
    public void testGetByIdWithPlayers() throws Exception {
        TeamDto returnedTeam = new TeamDto();
        when(teamService.getByIdWithPlayers(1L)).thenReturn(returnedTeam);

        mockMvc.perform(get("/teams/{id}", 1L)
                    .queryParam("with_players", "true")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(returnedTeam));
    }

    @Test
    @WithMockPrincipal
    public void testGetByIdWithoutPlayers() throws Exception {
        TeamDto returnedTeam = new TeamDto();
        when(teamService.getByIdWithoutPlayers(1L)).thenReturn(returnedTeam);

        mockMvc.perform(get("/teams/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(returnedTeam));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithPagination() throws Exception {
        PagingDataRespDto<TeamDto> pageResult = new PagingDataRespDto<>();

        when(teamService.getAll(any(PagingDataReqDto.class))).thenReturn(pageResult);

        mockMvc.perform(get("/teams")
                        .queryParam("size", "2")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(teamService).getAll(eq(new PagingDataReqDto(0, 2)));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithPagination_invalidParameters() throws Exception {
        mockMvc.perform(get("/teams")
                        .queryParam("size", "abc")
                        .queryParam("page", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithPagination_nullParameters() throws Exception {
        PagingDataRespDto<TeamDto> pageResult = new PagingDataRespDto<>();
        when(teamService.getAll(any(PagingDataReqDto.class))).thenReturn(pageResult);

        mockMvc.perform(get("/teams")
                        .queryParam("size", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(teamService).getAll(eq(new PagingDataReqDto(0, 100)));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithoutPagination() throws Exception {
        List<TeamDto> returnedTeams = new ArrayList<>();
        when(teamService.getAll()).thenReturn(returnedTeams);

        mockMvc.perform(get("/teams")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockPrincipal
    public void testGetAll_forbidden() throws Exception {
        mockMvc.perform(get("/teams")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockPrincipal
    public void testUpdate() throws Exception {
        TeamDto returnedTeam = new TeamDto();
        TeamUpdateDto updatedTeam = new TeamUpdateDto();
        updatedTeam.setName("Test Name");
        updatedTeam.setCountry("Spain");

        when(teamService.update(1L, updatedTeam)).thenReturn(returnedTeam);

        mockMvc.perform(patch("/teams/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedTeam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(returnedTeam));
    }

    @Test
    @WithMockPrincipal
    public void testUpdate_invalidParameters() throws Exception {
        TeamDto returnedTeam = new TeamDto();
        TeamUpdateDto updatedTeam = new TeamUpdateDto();
        updatedTeam.setName(" ");
        updatedTeam.setCountry("1234567890123456789012345678901234567890123456789012345678901");

        when(teamService.update(1L, updatedTeam)).thenReturn(returnedTeam);

        mockMvc.perform(patch("/teams/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedTeam)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields", hasSize(1)));
    }

    @Test
    @WithMockPrincipal
    public void testDelete_unAuthorized() throws Exception {
        mockMvc.perform(delete("/teams/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(mapper.writeValueAsString(new TeamUpdateDto())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testDelete() throws Exception {
        when(teamService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/teams/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}

