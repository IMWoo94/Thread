package com.imwoo.threads.exception.user;

import org.springframework.http.HttpStatus;

import com.imwoo.threads.exception.ClientErrorException;

public class UserNotAllowedException extends ClientErrorException {

	public UserNotAllowedException() {
		super(HttpStatus.FORBIDDEN, "User Not Allowed");
	}

}
