package com.toptal.soccermanager.controller;

import com.toptal.soccermanager.configuration.authentication.JwtUtils;
import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.AuthDto;
import com.toptal.soccermanager.model.dto.CredentialDto;
import com.toptal.soccermanager.model.dto.UserDto;
import com.toptal.soccermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/token")
    public ResponseEntity<AuthDto> login(@Valid @RequestBody CredentialDto credential) throws ApplicationException {
        UserDto loginUser = userService.login(credential);
        String token = jwtUtils.getJwtToken(loginUser);

        AuthDto auth = new AuthDto(loginUser.getId(), loginUser.getId(), token);

        return ResponseEntity.ok(auth);
    }
}
