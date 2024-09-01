package com.imwoo.threads.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.imwoo.threads.model.error.ClientErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ClientErrorException.class)
	public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
		return new ResponseEntity<>(
			new ClientErrorResponse(e.getHttpStatus(), e.getMessage()),
			e.getHttpStatus()
		);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<UndefinedErrorResponse> handleUndefinedRuntimeException(RuntimeException e) {
		return new ResponseEntity<>(
			new UndefinedErrorResponse(e.getMessage(), "정의 되지 않은 예외 입니다.\n 로그를 확인해주세요."),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

}
