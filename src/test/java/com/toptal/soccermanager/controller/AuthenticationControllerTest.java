package com.toptal.soccermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toptal.soccermanager.configuration.authentication.JwtUtils;
import com.toptal.soccermanager.model.dto.CredentialDto;
import com.toptal.soccermanager.model.dto.UserDto;
import com.toptal.soccermanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testLogin() throws Exception {
        UserDto mockedUser = new UserDto();
        mockedUser.setId(1L);

        String mockedToken = "MockedToken";
        when(userService.login(any(CredentialDto.class))).thenReturn(mockedUser);
        when(jwtUtils.getJwtToken(mockedUser)).thenReturn(mockedToken);

        CredentialDto credential = new CredentialDto();
        credential.setEmail("abc@xyz.com");
        credential.setPassword("pwd");

        mockMvc.perform(post("/auth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(credential)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("teamId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("token").value(mockedToken));
    }

    @Test
    public void testLogin_BadRequest() throws Exception {
        CredentialDto credential = new CredentialDto();
        credential.setEmail("abc");
        credential.setPassword("");

        mockMvc.perform(post("/auth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(credential)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errorFields", hasSize(2)));
    }
}
