package com.imwoo.threads.model.user.request;

public record UserSignUpRequest(
	String username,
	String password
) {
}
