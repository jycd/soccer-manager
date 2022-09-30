package com.toptal.soccermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toptal.soccermanager.model.dto.TransferCreateDto;
import com.toptal.soccermanager.model.dto.TransferDto;
import com.toptal.soccermanager.model.dto.TransferUpdateDto;
import com.toptal.soccermanager.service.TransferService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TeamTransferControllerTest {
    @MockBean
    private TransferService transferService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockPrincipal
    public void testCreate() throws Exception {
        TransferCreateDto createdTransfer = new TransferCreateDto();
        createdTransfer.setPlayerId(2L);
        createdTransfer.setAskPrice(10000.0);
        TransferDto returnedTransfer = new TransferDto();

        when(transferService.create(createdTransfer, 1L)).thenReturn(returnedTransfer);

        mockMvc.perform(post("/teams/{teamId}/transfers", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createdTransfer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(returnedTransfer));
    }

    @Test
    @WithMockPrincipal
    public void testCreate_invalidParameters() throws Exception {
        TransferCreateDto createdTransfer = new TransferCreateDto();
        createdTransfer.setPlayerId(2L);
        createdTransfer.setAskPrice(-0.01);
        TransferDto returnedTransfer = new TransferDto();

        when(transferService.create(createdTransfer, 1L)).thenReturn(returnedTransfer);

        mockMvc.perform(post("/teams/{teamId}/transfers", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createdTransfer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields", hasSize(1)));
    }

    @Test
    @WithMockPrincipal
    public void testCreate_unAuthorized() throws Exception {
        TransferCreateDto createdTransfer = new TransferCreateDto();
        createdTransfer.setPlayerId(2L);
        createdTransfer.setAskPrice(10000.0);
        TransferDto returnedTransfer = new TransferDto();

        mockMvc.perform(post("/teams/{teamId}/transfers", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createdTransfer)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockPrincipal
    public void testUpdate() throws Exception {
        TransferUpdateDto updatedTransfer = new TransferUpdateDto();
        updatedTransfer.setAskPrice(100000.0);
        TransferDto returnedTransfer = new TransferDto();

        when(transferService.update(2L, updatedTransfer, 1L)).thenReturn(returnedTransfer);

        mockMvc.perform(patch("/teams/{teamId}/transfers/{id}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedTransfer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(returnedTransfer));
    }

    @Test
    @WithMockPrincipal
    public void testUpdate_invalidParameters() throws Exception {
        TransferUpdateDto updatedTransfer = new TransferUpdateDto();
        updatedTransfer.setAskPrice(-100000.0);
        TransferDto returnedTransfer = new TransferDto();

        when(transferService.update(2L, updatedTransfer, 1L)).thenReturn(returnedTransfer);

        mockMvc.perform(patch("/teams/{teamId}/transfers/{id}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedTransfer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields", hasSize(1)));
    }

    @Test
    @WithMockPrincipal
    public void testDelete() throws Exception {
        when(transferService.delete(2L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/teams/{teamId}/transfers/{id}", 1L, 2L))
                .andExpect(status().isNoContent());
    }
}
