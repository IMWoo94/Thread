package com.imwoo.threads.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imwoo.threads.common.context.support.annotation.WithMockAdmin;
import com.imwoo.threads.config.TestWebSecurityConfiguration;
import com.imwoo.threads.model.PostCreateRequest;
import com.imwoo.threads.model.PostResponse;
import com.imwoo.threads.model.PostUpdateRequest;
import com.imwoo.threads.service.PostService;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(PostController.class)
@Import(TestWebSecurityConfiguration.class)
@Slf4j
class PostControllerTest {

	private final String BASIC_URL = "/api/v1/posts";
	private final String GET_POST_URL = "/api/v1/posts/1";
	@MockBean
	private PostService postService;
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String readJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

	@Test
	@DisplayName("Post 다건 조회")
	@WithMockAdmin
	void getPosts() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(BASIC_URL))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType("application/json"));

		Mockito.verify(postService, Mockito.times(1)).getPosts();
		Mockito.verify(postService, Mockito.only()).getPosts();
		Mockito.verify(postService, Mockito.timeout(3000)).getPosts();
	}

	@Test
	@DisplayName("Post 단건 조회")
	@WithMockAdmin
	void getPostByPostId() throws Exception {
		Mockito.when(postService.getPostByPostId(Mockito.anyLong()))
			.thenReturn(new PostResponse(1L, "test", ZonedDateTime.now(), ZonedDateTime.now(), null));

		mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_URL))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType("application/json"));

		Mockito.verify(postService, Mockito.times(1)).getPostByPostId(Mockito.anyLong());
		Mockito.verify(postService, Mockito.only()).getPostByPostId(Mockito.anyLong());
		Mockito.verify(postService, Mockito.timeout(3000)).getPostByPostId(Mockito.anyLong());
	}

	@Test
	@DisplayName("Post 신규 생성")
	@WithMockAdmin
	void newPostCreated() throws Exception {
		var requestBody = readJson(new PostCreateRequest("신규 포스트 생성 테스트 데이터"));

		Mockito.when(postService.createPost(Mockito.any()))
			.thenReturn(new PostResponse(1L, "test", ZonedDateTime.now(), ZonedDateTime.now(), null));

		mockMvc.perform(
			MockMvcRequestBuilders.post(BASIC_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());

		Mockito.verify(postService, Mockito.times(1)).createPost(Mockito.any());
		Mockito.verify(postService, Mockito.only()).createPost(Mockito.any());
		Mockito.verify(postService, Mockito.timeout(3000)).createPost(Mockito.any());
	}

	@Test
	@DisplayName("Post 수정 성공")
	@WithMockAdmin
	void updatePostOk() throws Exception {

		var requestBody = readJson(new PostUpdateRequest("수정 포스트"));

		Mockito.when(postService.updatePost(Mockito.anyLong(), Mockito.any()))
			.thenReturn(new PostResponse(1L, "test", ZonedDateTime.now(), ZonedDateTime.now(), null));

		mockMvc.perform(
			MockMvcRequestBuilders.patch(GET_POST_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());

		Mockito.verify(postService, Mockito.times(1)).updatePost(Mockito.anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.only()).updatePost(Mockito.anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.timeout(3000)).updatePost(Mockito.anyLong(), Mockito.any());
	}

	@Test
	@DisplayName("Post 수정 실패")
	@WithMockAdmin
	void updatePostFail() throws Exception {
		var requestBody = readJson(new PostUpdateRequest("수정 포스트"));

		Mockito.when(postService.updatePost(Mockito.anyLong(), Mockito.any()))
			.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

		mockMvc.perform(
				MockMvcRequestBuilders.patch(GET_POST_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(requestBody)
			).andDo(print())
			.andExpect(result -> {
				Assertions.assertTrue(result.getResolvedException() instanceof ResponseStatusException);
			});

		Mockito.verify(postService, Mockito.times(1)).updatePost(Mockito.anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.only()).updatePost(Mockito.anyLong(), Mockito.any());
		Mockito.verify(postService, Mockito.timeout(3000)).updatePost(Mockito.anyLong(), Mockito.any());
	}

	@Test
	@DisplayName("Post 삭제 성공")
	@WithMockAdmin
	void deletePostOk() throws Exception {
		Mockito.doNothing().when(postService).deletePost(Mockito.anyLong());

		mockMvc.perform(
				MockMvcRequestBuilders.delete(GET_POST_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
			).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isNoContent());

		Mockito.verify(postService, Mockito.times(1)).deletePost(Mockito.anyLong());
		Mockito.verify(postService, Mockito.only()).deletePost(Mockito.anyLong());
		Mockito.verify(postService, Mockito.timeout(3000)).deletePost(Mockito.anyLong());
	}

	@Test
	@DisplayName("Post 삭제 실패")
	@WithMockAdmin
	void deletePostFail() throws Exception {
		Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."))
			.when(postService)
			.deletePost(Mockito.anyLong());

		mockMvc.perform(
				MockMvcRequestBuilders.delete(GET_POST_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
			).andDo(print())
			.andExpect(result -> {
				Assertions.assertTrue(result.getResolvedException() instanceof ResponseStatusException);
			});

		Mockito.verify(postService, Mockito.times(1)).deletePost(Mockito.anyLong());
		Mockito.verify(postService, Mockito.only()).deletePost(Mockito.anyLong());
		Mockito.verify(postService, Mockito.timeout(3000)).deletePost(Mockito.anyLong());
	}

	@Test
	@DisplayName("Security Authorization Fail")
	@WithAnonymousUser
	void requestAccessDenied401() throws Exception {
		// given

		// when

		// then
		// 조회
		mockMvc.perform(MockMvcRequestBuilders.get(BASIC_URL))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// CUD
		mockMvc.perform(MockMvcRequestBuilders.post(BASIC_URL))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		mockMvc.perform(MockMvcRequestBuilders.patch(GET_POST_URL))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		mockMvc.perform(MockMvcRequestBuilders.delete(GET_POST_URL))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

}