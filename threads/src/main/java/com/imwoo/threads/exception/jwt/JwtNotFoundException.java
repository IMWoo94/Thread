package com.imwoo.threads.exception.jwt;

import io.jsonwebtoken.JwtException;

public class JwtNotFoundException extends JwtException {
	public JwtNotFoundException() {
		super("JWT Not Found");
	}

	public JwtNotFoundException(String message) {
		super(message);
	}

}
