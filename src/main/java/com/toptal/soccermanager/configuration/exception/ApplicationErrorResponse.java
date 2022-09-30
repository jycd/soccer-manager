package com.toptal.soccermanager.configuration.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationErrorResponse {
    private String status;
    private String description;
    private String message;
    private String error;
    private List<FieldError> errorFields;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static
    class FieldError {
        String field;
        String rejectReason;
    }
}
