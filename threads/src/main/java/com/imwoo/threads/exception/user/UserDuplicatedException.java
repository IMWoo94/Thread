package com.imwoo.threads.exception.user;

import org.springframework.http.HttpStatus;

import com.imwoo.threads.exception.ClientErrorException;

public class UserDuplicatedException extends ClientErrorException {

	public UserDuplicatedException() {
		super(HttpStatus.BAD_REQUEST, "User Already Exists");
	}

	public UserDuplicatedException(String username) {
		super(HttpStatus.BAD_REQUEST, "User With Username " + username + " Already Exists");
	}

}
