package com.imwoo.threads.exception.user;

import org.springframework.http.HttpStatus;

import com.imwoo.threads.exception.ClientErrorException;

public class UserNotFoundException extends ClientErrorException {

	public UserNotFoundException() {
		super(HttpStatus.NOT_FOUND, "User Not Found");
	}

	public UserNotFoundException(String username) {
		super(HttpStatus.NOT_FOUND, "User With Username " + username + " Not Found");
	}

}
