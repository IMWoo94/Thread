package com.imwoo.threads.exception.post;

import org.springframework.http.HttpStatus;

import com.imwoo.threads.exception.ClientErrorException;

public class PostCreatedFailureException extends ClientErrorException {
	public PostCreatedFailureException(Exception e) {
		super(HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
