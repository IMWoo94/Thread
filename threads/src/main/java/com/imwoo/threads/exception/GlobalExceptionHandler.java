package com.imwoo.threads.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.imwoo.threads.model.error.ClientErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ClientErrorException.class})
	public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
		// TODO : 전체적인 모든 에러 내용은 사용자에게 보여줄 필요는 없다. 차후 제공할 정보만 형식화하여 보여주자.
		return new ResponseEntity<>(
			new ClientErrorResponse(e.getHttpStatus(), e.getMessage()),
			e.getHttpStatus()
		);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ClientErrorResponse> handleClientErrorException(MethodArgumentNotValidException e) {
		return new ResponseEntity<>(
			new ClientErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()),
			HttpStatus.BAD_REQUEST
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
