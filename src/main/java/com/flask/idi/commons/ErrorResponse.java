package com.flask.idi.commons;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {

    private String message, code;

    private List<FieldError> filedErrors;

    @Data
    public static class FieldError {
        private String field, value, reason;
    }
}
