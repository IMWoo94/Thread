package com.imwoo.threads.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imwoo.threads.model.user.User;
import com.imwoo.threads.model.user.request.UserAuthenticateRequest;
import com.imwoo.threads.model.user.request.UserSignUpRequest;
import com.imwoo.threads.model.user.response.UserAuthenticationResponse;
import com.imwoo.threads.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {
		var user = userService.signUp(
			userSignUpRequest.username(),
			userSignUpRequest.password()
		);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<UserAuthenticationResponse> authenticate(
		@Valid @RequestBody UserAuthenticateRequest userAuthenticateRequest
	) {
		var response = userService.authenticate(
			userAuthenticateRequest.username(),
			userAuthenticateRequest.password()
		);
		return ResponseEntity.ok(response);
	}
}
