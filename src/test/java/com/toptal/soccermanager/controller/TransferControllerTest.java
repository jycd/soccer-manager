package com.toptal.soccermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toptal.soccermanager.model.dto.PagingDataReqDto;
import com.toptal.soccermanager.model.dto.PagingDataRespDto;
import com.toptal.soccermanager.model.dto.TransferDto;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerTest {
    @MockBean
    private TransferService transferService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockPrincipal
    public void testGetById() throws Exception {
        TransferDto returnedTransfer = new TransferDto();
        when(transferService.getById(1L)).thenReturn(returnedTransfer);

        mockMvc.perform(get("/transfers/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(returnedTransfer));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithPagination() throws Exception {
        PagingDataRespDto<TransferDto> pageResult = new PagingDataRespDto<>();

        when(transferService.getAll(any(PagingDataReqDto.class))).thenReturn(pageResult);

        mockMvc.perform(get("/transfers")
                        .queryParam("size", "2")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(transferService).getAll(eq(new PagingDataReqDto(0, 2)));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithPagination_invalidParameters() throws Exception {
        mockMvc.perform(get("/transfers")
                        .queryParam("size", "abc")
                        .queryParam("page", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithPagination_nullParameters() throws Exception {
        PagingDataRespDto<TransferDto> pageResult = new PagingDataRespDto<>();
        when(transferService.getAll(any(PagingDataReqDto.class))).thenReturn(pageResult);

        mockMvc.perform(get("/transfers")
                        .queryParam("size", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(transferService).getAll(eq(new PagingDataReqDto(0, 100)));
    }

    @Test
    @WithMockPrincipal
    public void testGetAllWithoutPagination() throws Exception {
        List<TransferDto> returnedTeams = new ArrayList<>();
        when(transferService.getAll()).thenReturn(returnedTeams);

        mockMvc.perform(get("/transfers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
