package com.toptal.soccermanager.configuration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApplicationError {
    USER_CREDENTIALS_INVALID(HttpStatus.BAD_REQUEST, "Incorrect email/password combination"),
    USER_DUPLICATED(HttpStatus.CONFLICT, "User with the mail already exists"),
    USER_BLOCKED(HttpStatus.BAD_REQUEST, "User is blocked. Please contact administrators"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User with the email is not found"),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Team with the given parameters is not found, maybe you deleted it before"),
    MORE_THAN_ONE_TEAM(HttpStatus.BAD_REQUEST, "User already owns a team, a user is allowed to have only one team"),
    PLAYER_NOT_FOUND(HttpStatus.NOT_FOUND, "Player with the given parameters is not found, maybe you deleted it before"),
    FILTER_CRITERIA_ERROR(HttpStatus.BAD_REQUEST, "Incorrect filter criteria"),
    TRANSFER_DUPLICATED(HttpStatus.CONFLICT, "Player with the given parameters already exists in transfer list"),
    TRANSFER_NOT_FOUND(HttpStatus.NOT_FOUND, "Transfer with the given parameters is not found, the player was already moved out of the transfer list"),
    TEAM_INSUFFICIENT_BUDGET(HttpStatus.BAD_REQUEST, "Team budget is not sufficient to buy the player"),
    UNAUTHORIZED_USER_ERROR(HttpStatus.UNAUTHORIZED, "User is not permitted to do this action on this data"),
    REQUEST_PARAMETERS_NOT_VALID(HttpStatus.BAD_REQUEST, "One or more required fields are invalid");

    private final HttpStatus httpStatus;
    private final String description;

    ApplicationError(final HttpStatus httpStatus, final String description) {
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
