package com.imwoo.threads.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imwoo.threads.common.context.support.annotation.WithMockAdmin;
import com.imwoo.threads.config.TestWebSecurityConfiguration;
import com.imwoo.threads.exception.user.UserDuplicatedException;
import com.imwoo.threads.exception.user.UserNotAllowedException;
import com.imwoo.threads.exception.user.UserNotFoundException;
import com.imwoo.threads.model.entity.UserEntity;
import com.imwoo.threads.model.post.response.PostResponse;
import com.imwoo.threads.model.user.User;
import com.imwoo.threads.model.user.request.UserAuthenticateRequest;
import com.imwoo.threads.model.user.request.UserSignUpRequest;
import com.imwoo.threads.model.user.request.UserUpdateRequest;
import com.imwoo.threads.model.user.response.UserAuthenticationResponse;
import com.imwoo.threads.service.PostService;
import com.imwoo.threads.service.UserService;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(UserController.class)
@Import(TestWebSecurityConfiguration.class)
@Slf4j
class UserControllerTest {

	/**
	 * TestWebSecurityConfiguration 안에 UserService Mock 존재
	 * @MockBean
	 * private UserService userService;
	 */

	@Autowired
	private UserService userService;
	@MockBean
	private PostService postService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String readJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

	/**
	 * Spring Security 인증 불필요 요청 테스트 케이스
	 */

	@Test
	@DisplayName("[Success] 회원 가입 요청 테스트")
	void singUpRequestTestSuccess() throws Exception {
		// given
		var requestBody = readJson(new UserSignUpRequest("admin", "admin"));
		var url = "/api/v1/users";
		var user = User.from(
			new UserEntity(1L, "admin", "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(userService.signUp(anyString(), anyString())).thenReturn(user);

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());

		// then
		Mockito.verify(userService, Mockito.times(1)).signUp(anyString(), anyString());
		Mockito.verify(userService, Mockito.only()).signUp(anyString(), anyString());
		Mockito.verify(userService, Mockito.timeout(3000)).signUp(anyString(), anyString());
	}

	/**
	 * 회원 가입 요청 실패 케이스
	 * 1. UserSignUpRequest 파라미터 Validation Error
	 * 2. username 동일 중복 회원 UserDuplicatedException Error
	 */
	@Test
	@DisplayName("[Failure] 회원 가입 요청 파라미터 검증 테스트")
	void singUpRequestParamValidTestFailure() throws Exception {
		// given
		var requestBody = readJson(new UserSignUpRequest(null, "admin"));
		var url = "/api/v1/users";

		// mocking
		// Controller 파라미터 바인딩 과정에서 Validation Error 발생하기에 별도의 Mocking 은 불필요
		// when(userService.signUp(anyString(), anyString())).thenThrow(MethodArgumentNotValidException.class);

		// when
		mockMvc.perform(MockMvcRequestBuilders.post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding(StandardCharsets.UTF_8)
			.content(requestBody)
		).andExpect(result -> {
			Assertions.assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException());
		});

		// then
		Mockito.verify(userService, Mockito.times(0)).signUp(anyString(), anyString());
	}

	@Test
	@DisplayName("[Failure] 회원 가입 요청 중복 검증 테스트")
	void singUpRequestDuplicateTestFailure() throws Exception {
		// given
		var requestBody = readJson(new UserSignUpRequest("duplicate", "duplicate"));
		var url = "/api/v1/users";

		// mocking
		when(userService.signUp(anyString(), anyString())).thenThrow(new UserDuplicatedException("duplicate"));

		// when
		mockMvc.perform(MockMvcRequestBuilders.post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding(StandardCharsets.UTF_8)
			.content(requestBody)
		).andExpect(result -> {
			Assertions.assertInstanceOf(UserDuplicatedException.class, result.getResolvedException());
		});

		// then
		Mockito.verify(userService, Mockito.times(1)).signUp(anyString(), anyString());
		Mockito.verify(userService, Mockito.only()).signUp(anyString(), anyString());
		Mockito.verify(userService, Mockito.timeout(3000)).signUp(anyString(), anyString());
	}

	@Test
	@DisplayName("[Success] 회원 인증 요청 테스트")
	void authenticateRequestTestSuccess() throws Exception {
		// given
		var requestBody = readJson(new UserAuthenticateRequest("admin", "admin"));
		var url = "/api/v1/users/authenticate";
		var accessToken = "json web token";

		// mocking
		Mockito.when(userService.authenticate(anyString(), anyString()))
			.thenReturn(new UserAuthenticationResponse(accessToken));

		// when
		mockMvc.perform(MockMvcRequestBuilders.post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding(StandardCharsets.UTF_8)
			.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());

		// then
		Mockito.verify(userService, Mockito.times(1)).authenticate(anyString(), anyString());
		Mockito.verify(userService, Mockito.only()).authenticate(anyString(), anyString());
		Mockito.verify(userService, Mockito.timeout(3000)).authenticate(anyString(), anyString());
	}

	/**
	 * 회원 가입 요청 실패 케이스
	 * 1. UserAuthenticateRequest 파라미터 Validation Error
	 * 2. username UserNotFoundException Error
	 */
	@Test
	@DisplayName("[Failure] 회원 인증 요청 파라미터 검증 테스트")
	void authenticateRequestParmaValidTestFailure() throws Exception {
		// given
		var requestBody = readJson(new UserAuthenticateRequest(null, "admin"));
		var url = "/api/v1/users/authenticate";

		// mocking
		// Controller Validation 검증으로 Mocking 불필요

		// when
		mockMvc.perform(MockMvcRequestBuilders.post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding(StandardCharsets.UTF_8)
			.content(requestBody)
		).andExpect(result -> {
			Assertions.assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException());
		});

		// then
		Mockito.verify(userService, Mockito.times(0)).authenticate(anyString(), anyString());
	}

	@Test
	@DisplayName("[Failure] 회원 인증 요청 Not Found 테스트")
	void authenticateRequestNotFoundTestFailure() throws Exception {
		// given
		var requestBody = readJson(new UserAuthenticateRequest("admin", "admin"));
		var url = "/api/v1/users/authenticate";

		// mocking
		when(userService.authenticate(anyString(), anyString())).thenThrow(new UserNotFoundException("admin"));

		// when
		mockMvc.perform(MockMvcRequestBuilders.post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding(StandardCharsets.UTF_8)
			.content(requestBody)
		).andExpect(result -> {
			Assertions.assertInstanceOf(UserNotFoundException.class, result.getResolvedException());
		});

		// then
		Mockito.verify(userService, Mockito.times(1)).authenticate(anyString(), anyString());
		Mockito.verify(userService, Mockito.only()).authenticate(anyString(), anyString());
		Mockito.verify(userService, Mockito.timeout(3000)).authenticate(anyString(), anyString());
	}

	@Test
	@DisplayName("[Success] 회원 조회 전체 요청 테스트")
	@WithMockAdmin
	void userSearchQueryNotExistsRequestTestSuccess() throws Exception {
		// given
		var url = "/api/v1/users";
		var users = List.of(
			User.from(
				new UserEntity(1L, "admin", "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null)),
			User.from(
				new UserEntity(2L, "a", "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null))
		);

		// mocking
		when(userService.getUsers(anyString())).thenReturn(users);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
			).andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());

		// then
		Mockito.verify(userService, Mockito.times(1)).getUsers(null);
		Mockito.verify(userService, Mockito.only()).getUsers(null);
	}

	@Test
	@DisplayName("[Success] 회원 조회 Like 요청 테스트")
	@WithMockAdmin
	void userSearchQueryExistsRequestTestSuccess() throws Exception {
		// given
		var query = "a";
		var url = "/api/v1/users";
		var users = List.of(
			User.from(
				new UserEntity(1L, "admin", "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null)),
			User.from(
				new UserEntity(2L, "a", "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null))
		);

		// mocking
		when(userService.getUsers(anyString())).thenReturn(users);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
					.param("query", query)
			).andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());

		// then
		Mockito.verify(userService, Mockito.times(1)).getUsers(anyString());
		Mockito.verify(userService, Mockito.only()).getUsers(anyString());
	}

	@Test
	@DisplayName("[Success] 회원 조회 단건 요청 테스트")
	@WithMockAdmin
	void userSearchUsernameRequestTestSuccess() throws Exception {
		// given
		var username = "admin";
		var url = "/api/v1/users/";
		var user = User.from(
			new UserEntity(1L, "admin", "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(userService.getUser(anyString())).thenReturn(user);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url + username)
			).andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());

		// then
		Mockito.verify(userService, Mockito.times(1)).getUser(anyString());
		Mockito.verify(userService, Mockito.only()).getUser(anyString());
	}

	@Test
	@DisplayName("[Failure] 회원 조회 단건 요청 테스트")
	@WithMockAdmin
	void userSearchUsernameRequestNotFoundTestFailure() throws Exception {
		// given
		var username = "admin";
		var url = "/api/v1/users/";
		var user = User.from(
			new UserEntity(1L, "admin", "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(userService.getUser(anyString())).thenThrow(new UserNotFoundException(username));

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.get(url + username)
		).andExpect(result -> {
			Assertions.assertInstanceOf(UserNotFoundException.class, result.getResolvedException());
		}).andDo(print());

		// then
		Mockito.verify(userService, Mockito.times(1)).getUser(anyString());
		Mockito.verify(userService, Mockito.only()).getUser(anyString());
	}

	@Test
	@DisplayName("[Success] 회원 변경 요청 테스트")
	@WithMockAdmin
	void userUpdateRequestTestSuccess() throws Exception {
		// given
		var username = "admin";
		var description = "update description";
		var requestBody = readJson(new UserUpdateRequest(description));
		var url = "/api/v1/users/";
		var user = User.from(
			new UserEntity(1L, username, "admin", null, description, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(userService.updateUser(anyString(), any(), any())).thenReturn(user);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.patch(url + username)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(requestBody)
			).andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());

		// then
		Mockito.verify(userService, Mockito.times(1)).updateUser(anyString(), any(), any());
		Mockito.verify(userService, Mockito.only()).updateUser(anyString(), any(), any());
	}

	@Test
	@DisplayName("[Failure] 회원 변경 요청 Not Found 테스트")
	@WithMockAdmin
	void userUpdateRequestNotFoundTestFailure() throws Exception {
		// given
		var username = "admin";
		var description = "update description";
		var requestBody = readJson(new UserUpdateRequest(description));
		var url = "/api/v1/users/";

		// mocking
		when(userService.updateUser(anyString(), any(), any())).thenThrow(new UserNotFoundException(username));

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.patch(url + username)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(requestBody)
		).andExpect(result -> {
			Assertions.assertInstanceOf(UserNotFoundException.class, result.getResolvedException());
		}).andDo(print());

		// then
		Mockito.verify(userService, Mockito.times(1)).updateUser(anyString(), any(), any());
		Mockito.verify(userService, Mockito.only()).updateUser(anyString(), any(), any());
	}

	@Test
	@DisplayName("[Failure] 회원 변경 요청 Not Allowed 테스트")
	@WithMockAdmin
	void userUpdateRequestNotAllowedTestFailure() throws Exception {
		// given
		var username = "admin";
		var description = "update description";
		var requestBody = readJson(new UserUpdateRequest(description));
		var url = "/api/v1/users/";

		// mocking
		when(userService.updateUser(anyString(), any(), any())).thenThrow(new UserNotAllowedException());

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.patch(url + username)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(requestBody)
		).andExpect(result -> {
			Assertions.assertInstanceOf(UserNotAllowedException.class, result.getResolvedException());
		}).andDo(print());

		// then
		Mockito.verify(userService, Mockito.times(1)).updateUser(anyString(), any(), any());
		Mockito.verify(userService, Mockito.only()).updateUser(anyString(), any(), any());
	}

	@Test
	@DisplayName("[Success] 회원 게시글 요청 테스트")
	@WithMockAdmin
	void userGetPostsRequestTestSuccess() throws Exception {
		// given
		var username = "admin";
		var url = "/api/v1/users/" + username + "/posts";
		var user = User.from(
			new UserEntity(1L, username, "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		var posts = List.of(new PostResponse(1L, "test", user, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(postService.getPostsByUsername(anyString())).thenReturn(posts);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
			).andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());

		// then
		Mockito.verify(postService, Mockito.times(1)).getPostsByUsername(anyString());
		Mockito.verify(postService, Mockito.only()).getPostsByUsername(anyString());
	}

	@Test
	@DisplayName("[Failure] 회원 게시글 요청 Not Found 테스트")
	@WithMockAdmin
	void userGetPostsRequestNotFoundTestFailure() throws Exception {
		// given
		var username = "admin";
		var url = "/api/v1/users/" + username + "/posts";
		var user = User.from(
			new UserEntity(1L, username, "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		var posts = List.of(new PostResponse(1L, "test", user, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(postService.getPostsByUsername(anyString())).thenThrow(new UserNotFoundException(username));

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.get(url)
		).andExpect(result -> {
			Assertions.assertInstanceOf(UserNotFoundException.class, result.getResolvedException());
		}).andDo(print());

		// then
		Mockito.verify(postService, Mockito.times(1)).getPostsByUsername(anyString());
		Mockito.verify(postService, Mockito.only()).getPostsByUsername(anyString());
	}

}