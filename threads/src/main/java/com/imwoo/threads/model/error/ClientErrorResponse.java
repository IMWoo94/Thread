package com.imwoo.threads.model.error;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClientErrorResponse(HttpStatus status, Object message) {
}
