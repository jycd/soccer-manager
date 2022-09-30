package com.toptal.soccermanager.configuration.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toptal.soccermanager.configuration.exception.ApplicationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
@ControllerAdvice
@Slf4j
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Authentication error: " + authException.getMessage());

        ApplicationErrorResponse errorResponse = new ApplicationErrorResponse();
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.name());
        errorResponse.setDescription(authException.getMessage());
        errorResponse.setError(HttpStatus.UNAUTHORIZED.name());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(convertObjectToJson(errorResponse));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void commence(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("AccessDenied error: " + accessDeniedException.getMessage());

        ApplicationErrorResponse errorResponse = new ApplicationErrorResponse();
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.name());
        errorResponse.setDescription(accessDeniedException.getMessage());
        errorResponse.setError(HttpStatus.UNAUTHORIZED.name());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(convertObjectToJson(errorResponse));
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}

