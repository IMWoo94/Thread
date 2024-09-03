package com.imwoo.threads.controller;

import static com.imwoo.threads.config.TestWebSecurityConfiguration.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imwoo.threads.common.context.support.annotation.WithMockAdmin;
import com.imwoo.threads.config.TestWebSecurityConfiguration;
import com.imwoo.threads.exception.post.PostNotFoundException;
import com.imwoo.threads.model.post.request.PostCreateRequest;
import com.imwoo.threads.model.post.request.PostUpdateRequest;
import com.imwoo.threads.model.post.response.PostResponse;
import com.imwoo.threads.service.JwtService;
import com.imwoo.threads.service.PostService;
import com.imwoo.threads.service.UserService;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(PostController.class)
@Import(TestWebSecurityConfiguration.class)
@Slf4j
class PostControllerTest {

	private final static String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;
	private final static String CHARSET_UTF8 = StandardCharsets.UTF_8.name();
	@MockBean
	private PostService postService;

	/**
	 * Jwt CustomFilter 등록에 의해서 MockBean 처리
	 */
	@MockBean
	private JwtService jwtService;
	@MockBean
	private UserService userService;
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String readJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

	@Test
	@DisplayName("[Success] 전체 Post 조회 요청 테스트")
	@WithMockAdmin
	void getMultiPostRequestTestSuccess() throws Exception {
		// given
		var url = "/api/v1/posts";

		// when
		mockMvc.perform(MockMvcRequestBuilders.get(url))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON));

		// then
		Mockito.verify(postService, Mockito.times(1)).getPosts();
		Mockito.verify(postService, Mockito.only()).getPosts();
		Mockito.verify(postService, Mockito.timeout(3000)).getPosts();
	}

	@Test
	@DisplayName("[Success] 단건 Post 조회 요청 테스트")
	@WithMockAdmin
	void getSinglePostRequestTestSuccess() throws Exception {
		// given
		var url = "/api/v1/posts/1";

		// mocking
		Mockito.when(postService.getPostByPostId(anyLong()))
			.thenReturn(new PostResponse(1L, "test", ZonedDateTime.now(), ZonedDateTime.now(), null));

		// when
		mockMvc.perform(MockMvcRequestBuilders.get(url))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON));

		// then
		Mockito.verify(postService, Mockito.times(1)).getPostByPostId(anyLong());
		Mockito.verify(postService, Mockito.only()).getPostByPostId(anyLong());
		Mockito.verify(postService, Mockito.timeout(3000)).getPostByPostId(anyLong());
	}

	@Test
	@DisplayName("[Success] Post 신규 생성 요청 테스트")
	@WithMockAdmin
	void newCreatePostRequestTestSuccess() throws Exception {
		// given
		var requestBody = readJson(new PostCreateRequest("신규 포스트 생성 테스트 데이터"));
		var url = "/api/v1/posts";

		// mocking
		Mockito.when(postService.createPost(any()))
			.thenReturn(new PostResponse(1L, "test", ZonedDateTime.now(), ZonedDateTime.now(), null));

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.contentType(CONTENT_TYPE_JSON)
				.characterEncoding(CHARSET_UTF8)
				.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());

		// then
		Mockito.verify(postService, Mockito.times(1)).createPost(Mockito.any());
		Mockito.verify(postService, Mockito.only()).createPost(Mockito.any());
		Mockito.verify(postService, Mockito.timeout(3000)).createPost(Mockito.any());
	}

	@Test
	@DisplayName("[Failure] Post 신규 생성 요청 테스트")
	@WithMockAdmin
	void newCreatePostRequestTestFailure() throws Exception {
		// given
		var requestBody = readJson(new PostCreateRequest("신규 포스트 생성 테스트 데이터"));
		var url = "/api/v1/posts";

		// mocking
		Mockito.when(postService.createPost(any()))
			.thenThrow(new PostNotFoundException());

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.contentType(CONTENT_TYPE_JSON)
				.characterEncoding(CHARSET_UTF8)
				.content(requestBody)
		).andExpect(result -> {
			Assertions.assertInstanceOf(PostNotFoundException.class, result.getResolvedException());
		});

		// then
		Mockito.verify(postService, Mockito.times(1)).createPost(Mockito.any());
		Mockito.verify(postService, Mockito.only()).createPost(Mockito.any());
		Mockito.verify(postService, Mockito.timeout(3000)).createPost(Mockito.any());
	}

	@Test
	@DisplayName("[Success] Post 수정 요청 테스트")
	@WithMockAdmin
	void updatedPostRequestTestSuccess() throws Exception {
		// given
		var requestBody = readJson(new PostUpdateRequest("수정 포스트"));
		var url = "/api/v1/posts/";

		// mocking
		Mockito.when(postService.updatePost(anyLong(), any()))
			.thenReturn(new PostResponse(1L, "test", ZonedDateTime.now(), ZonedDateTime.now(), null));

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.patch(url + 1)
				.contentType(CONTENT_TYPE_JSON)
				.characterEncoding(CHARSET_UTF8)
				.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());

		// then
		Mockito.verify(postService, Mockito.times(1)).updatePost(anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.only()).updatePost(anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.timeout(3000)).updatePost(anyLong(), Mockito.any());
	}

	@Test
	@DisplayName("[Failure] Post 수정 요청 테스트")
	@WithMockAdmin
	void updatedPostRequestTestFailure() throws Exception {
		// given
		var requestBody = readJson(new PostUpdateRequest("수정 포스트"));
		var url = "/api/v1/posts/";

		// mocking
		Mockito.when(postService.updatePost(anyLong(), any()))
			.thenThrow(new PostNotFoundException());

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.patch(url + 1)
					.contentType(CONTENT_TYPE_JSON)
					.characterEncoding(CHARSET_UTF8)
					.content(requestBody)
			).andDo(print())
			.andExpect(result -> {
				Assertions.assertInstanceOf(PostNotFoundException.class, result.getResolvedException());
			});

		// then
		Mockito.verify(postService, Mockito.times(1)).updatePost(anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.only()).updatePost(anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.timeout(3000)).updatePost(anyLong(), Mockito.any());
	}

	@Test
	@DisplayName("[Success] Post 삭제 요청 테스트")
	@WithMockAdmin
	void deletedPostRequestTestSuccess() throws Exception {
		// given
		var url = "/api/v1/posts/";

		// mocking
		Mockito.doNothing()
			.when(postService)
			.deletePost(anyLong());

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.delete(url + anyLong())
					.contentType(CONTENT_TYPE_JSON)
					.characterEncoding(CHARSET_UTF8)
			).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isNoContent());

		// then
		Mockito.verify(postService, Mockito.times(1)).deletePost(anyLong());
		Mockito.verify(postService, Mockito.only()).deletePost(anyLong());
		Mockito.verify(postService, Mockito.timeout(3000)).deletePost(anyLong());
	}

	@Test
	@DisplayName("[Failure] Post 삭제 요청 테스트")
	@WithMockAdmin
	void deletedPostRequestTestFailure() throws Exception {
		// given
		var url = "/api/v1/posts/";

		// mocking
		Mockito.doThrow(new PostNotFoundException())
			.when(postService)
			.deletePost(anyLong());

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.delete(url + anyLong())
					.contentType(CONTENT_TYPE_JSON)
					.characterEncoding(CHARSET_UTF8)
			).andDo(print())
			.andExpect(result -> {
				Assertions.assertInstanceOf(PostNotFoundException.class, result.getResolvedException());
			});

		// then
		Mockito.verify(postService, Mockito.times(1)).deletePost(anyLong());
		Mockito.verify(postService, Mockito.only()).deletePost(anyLong());
		Mockito.verify(postService, Mockito.timeout(3000)).deletePost(anyLong());
	}

	@Test
	@DisplayName("[Failure] Authorization 요청 테스트")
	@WithAnonymousUser
	void authorizationRequestAccessDenied401() throws Exception {
		// given
		var url = "/api/v1/posts";

		// when
		mockMvc.perform(MockMvcRequestBuilders.get(url))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		mockMvc.perform(MockMvcRequestBuilders.post(url))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		mockMvc.perform(MockMvcRequestBuilders.patch(url + 1))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		mockMvc.perform(MockMvcRequestBuilders.delete(url + 1))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// then
	}

	@Test
	@DisplayName("[Success] CORS 요청 테스트")
	@WithMockAdmin
	void CORSRequestAccessSuccess() throws Exception {
		// given
		var url = "/api/v1/posts";
		var origin = ORIGIN_URL;

		// when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.ORIGIN, origin))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));

		// then
	}

	@Test
	@DisplayName("[Failure] CORS 요청 테스트")
	@WithMockAdmin
	void CORSRequestAccessFailure() throws Exception {
		// given
		var url = "/api/v1/posts";
		var origin = "https://non-baeldung.com";

		// when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.ORIGIN, origin))
			.andExpect(status().isForbidden())
			.andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));

		// then
	}

}