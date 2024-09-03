package com.imwoo.threads.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UndefinedErrorResponse(String error, String message) {
}
