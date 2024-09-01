package com.imwoo.threads.exception.post;

import org.springframework.http.HttpStatus;

import com.imwoo.threads.exception.ClientErrorException;

public class PostNotFoundException extends ClientErrorException {

	public PostNotFoundException() {
		super(HttpStatus.NOT_FOUND, "Post Not Found");
	}

	public PostNotFoundException(Long postId) {
		super(HttpStatus.NOT_FOUND, "Post With PostId " + postId + " Not Found");
	}

	public PostNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
