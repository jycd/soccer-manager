package com.toptal.soccermanager.controller;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.service.UserService;
import com.toptal.soccermanager.utils.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    // This endpoint is for sign up and team creation
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto user) throws ApplicationException {
        UserDto signUpUser = userService.create(user);

        return new ResponseEntity<>(signUpUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #id == authentication.principal.id)")
    public ResponseEntity<UserDto> getById(@PathVariable("id") long id) throws ApplicationException {
        UserDto existUser = userService.getById(id);

        return ResponseEntity.ok(existUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAll(@Pattern(regexp = "[\\s]*[0-9]*[1-9]+[0-9]*[\\s]*", message = "size must be greater than 0") @RequestParam(required = false, name = "size") String size,
                                    @Pattern(regexp = "[\\s]*[0-9]+[\\s]*", message = "page must be greater than or equal to 0") @RequestParam(required = false, name = "page") String page) throws ApplicationException {
        if (size == null && page == null) {
            List<UserDto> existUsers = userService.getAll();
            return ResponseEntity.ok(existUsers);
        } else {
            int pageSize;
            int pageNumber;
            if (size == null) {
                pageSize = 50;
            } else {
                pageSize = Integer.parseInt(size.trim());
            }
            if (page == null) {
                pageNumber = 0;
            } else {
                pageNumber = Integer.parseInt(page.trim());
            }
            if (pageSize > 100) {
                pageSize = 100;
            }
            PagingDataRespDto<UserDto> existUser = userService.getAll(new PagingDataReqDto(pageNumber, pageSize));
            return ResponseEntity.ok(existUser);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #id == #principal.id)")
    public ResponseEntity<UserDto> update(@PathVariable("id") long id, @Valid @RequestBody UserUpdateDto user,
                                          @AuthenticationPrincipal UserInfo principal) throws ApplicationException {
        String principalRole = principal.getAuthorities().stream().map(Object::toString).findFirst().get();
        user.setRole(
                user.getRole() == null || principalRole.equalsIgnoreCase("ROLE_USER") ? null : user.getRole()
        );
        user.setStatus(
                user.getStatus() == null || principalRole.equalsIgnoreCase("ROLE_USER") ? null : user.getStatus()
        );
        UserDto updatedUser = userService.update(id, user);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #id == authentication.principal.id)")
    public ResponseEntity delete(@PathVariable("id") long id) throws ApplicationException {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
