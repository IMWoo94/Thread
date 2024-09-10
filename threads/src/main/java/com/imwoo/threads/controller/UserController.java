package com.imwoo.threads.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imwoo.threads.model.entity.UserEntity;
import com.imwoo.threads.model.post.response.PostResponse;
import com.imwoo.threads.model.user.User;
import com.imwoo.threads.model.user.request.UserAuthenticateRequest;
import com.imwoo.threads.model.user.request.UserSignUpRequest;
import com.imwoo.threads.model.user.request.UserUpdateRequest;
import com.imwoo.threads.model.user.response.UserAuthenticationResponse;
import com.imwoo.threads.service.PostService;
import com.imwoo.threads.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final PostService postService;

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

	@GetMapping
	public ResponseEntity<List<User>> getUsers(
		@RequestParam(required = false) String query
	) {
		var users = userService.getUsers(query);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> getUser(
		@PathVariable String username
	) {
		return ResponseEntity.ok(userService.getUser(username));
	}

	@PatchMapping("/{username}")
	public ResponseEntity<User> updateUser(
		@PathVariable String username,
		@RequestBody UserUpdateRequest userUpdateRequest,
		Authentication authentication
	) {
		var user = userService.updateUser(username, userUpdateRequest, (UserEntity)authentication.getPrincipal());
		return ResponseEntity.ok(user);
	}

	// GET /users/{username}/posts
	@GetMapping("/{username}/posts")
	public ResponseEntity<List<PostResponse>> getPostsByUsername(
		@PathVariable String username
	) {
		return ResponseEntity.ok(postService.getPostsByUsername(username));
	}
}
