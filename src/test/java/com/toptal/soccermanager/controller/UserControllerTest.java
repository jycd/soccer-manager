package com.toptal.soccermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.service.UserService;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCreate() throws Exception {
        UserDto returnedUserDto = getUserDto(1L);

        UserCreateDto createdUserDto = new UserCreateDto();
        createdUserDto.setEmail(returnedUserDto.getEmail());
        createdUserDto.setFullName(returnedUserDto.getFullName());
        createdUserDto.setPassword("test");
        createdUserDto.setRole(returnedUserDto.getRole());

        when(userService.create(any(UserCreateDto.class))).thenReturn(returnedUserDto);

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createdUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnedUserDto.getId()))
                .andExpect(jsonPath("$.email", is(returnedUserDto.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.fullName", is(returnedUserDto.getFullName())))
                .andExpect(jsonPath("$.role", is(returnedUserDto.getRole())))
                .andExpect(jsonPath("$.status", is(returnedUserDto.getStatus())));

        verify(userService).create(eq(createdUserDto));
    }

    @Test
    public void testCreate_invalidParameters() throws Exception {
        UserCreateDto createdUserDto = new UserCreateDto();
        createdUserDto.setEmail("123");
        createdUserDto.setFullName("  ");
        createdUserDto.setPassword(" ");
        createdUserDto.setRole("ROLE_ADMIN");

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createdUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields", hasSize(4)));
    }

    @Test
    @WithMockPrincipal
    public void testGetById() throws Exception {
        UserDto returnedUserDto = getUserDto(1L);
        when(userService.getById(anyLong())).thenReturn(returnedUserDto);

        mockMvc.perform(get("/users/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedUserDto.getId()))
                .andExpect(jsonPath("$.email", is(returnedUserDto.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.fullName", is(returnedUserDto.getFullName())))
                .andExpect(jsonPath("$.role", is(returnedUserDto.getRole())))
                .andExpect(jsonPath("$.status", is(returnedUserDto.getStatus())));

        verify(userService).getById(eq(1L));
    }

    @Test
    @WithMockPrincipal
    public void testGetById_forbidden() throws Exception {
        mockMvc.perform(get("/users/{id}", 2l)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithPagination() throws Exception {
        PagingDataRespDto<UserDto> pageResult = new PagingDataRespDto<>();

        when(userService.getAll(any(PagingDataReqDto.class))).thenReturn(pageResult);

        mockMvc.perform(get("/users")
                    .queryParam("size", "2")
                    .queryParam("page", "0")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(userService).getAll(eq(new PagingDataReqDto(0, 2)));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithPagination_invalidParameters() throws Exception {
        mockMvc.perform(get("/users")
                        .queryParam("size", "abc")
                        .queryParam("page", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithPagination_nullParameters() throws Exception {
        PagingDataRespDto<UserDto> pageResult = new PagingDataRespDto<>();
        when(userService.getAll(any(PagingDataReqDto.class))).thenReturn(pageResult);

        mockMvc.perform(get("/users")
                    .queryParam("size", "101")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(pageResult));

        verify(userService).getAll(eq(new PagingDataReqDto(0, 100)));
    }

    @Test
    @WithMockPrincipal(role = "ROLE_ADMIN")
    public void testGetAllWithoutPagination() throws Exception {
        List<UserDto> returnedUsers = new ArrayList<>(Arrays.asList(getUserDto(1L)));
        when(userService.getAll()).thenReturn(returnedUsers);

        mockMvc.perform(get("/users")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(returnedUsers.get(0).getId()))
                .andExpect(jsonPath("$[0].email", is(returnedUsers.get(0).getEmail())))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].fullName", is(returnedUsers.get(0).getFullName())))
                .andExpect(jsonPath("$[0].role", is(returnedUsers.get(0).getRole())))
                .andExpect(jsonPath("$[0].status", is(returnedUsers.get(0).getStatus())));
    }

    @Test
    @WithMockPrincipal
    public void testGetAll_forbidden() throws Exception {
        mockMvc.perform(get("/users")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockPrincipal
    public void testUpdate_invalidParameters() throws Exception {
        UserDto returnedUser = getUserDto(1L);
        UserUpdateDto updatedUser = new UserUpdateDto();
        updatedUser.setEmail("123");
        updatedUser.setPassword("");
        updatedUser.setFullName("123456789012345678901");
        updatedUser.setRole("ROLE_ADMIN");
        updatedUser.setStatus("ab");

        when(userService.update(1L, updatedUser)).thenReturn(returnedUser);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields", hasSize(3)));
    }

    @Test
    @WithMockPrincipal
    public void testDelete() throws Exception {
        when(userService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete_unAuthorized() throws Exception {
        mockMvc.perform(delete("/users/{id}", 2L))
                .andExpect(status().isUnauthorized());
    }

    private UserDto getUserDto(long id) {
        String email = "abc@xyz.com";
        String fullName = "John Smith";
        String role = "ROLE_USER";
        String status = "ACTIVE";

        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setEmail(email);
        userDto.setFullName(fullName);
        userDto.setRole(role);
        userDto.setStatus(status);

        return userDto;
    }
}

