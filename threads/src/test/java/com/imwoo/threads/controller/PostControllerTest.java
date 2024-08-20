package com.imwoo.threads.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imwoo.threads.model.Post;
import com.imwoo.threads.model.PostCreateRequest;
import com.imwoo.threads.model.PostUpdateRequest;
import com.imwoo.threads.service.PostService;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(PostController.class)
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
	void getPosts() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(BASIC_URL))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType("application/json"));
	}

	@Test
	@DisplayName("Post 단건 조회")
	void getPostByPostId() throws Exception {
		Mockito.when(postService.getPostByPostId(Mockito.anyLong()))
			.thenReturn(Optional.of(new Post(1L, "test", ZonedDateTime.now())));

		mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_URL))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType("application/json"));
	}

	@Test
	@DisplayName("Post 신규 생성")
	void newPostCreated() throws Exception {

		var requestBody = readJson(new PostCreateRequest("신규 포스트 생성 테스트 데이터"));

		Mockito.when(postService.createPost(Mockito.any()))
			.thenReturn(new Post(1L, "test", ZonedDateTime.now()));

		mockMvc.perform(
			MockMvcRequestBuilders.post(BASIC_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Post 수정 성공")
	void updatePostOk() throws Exception {

		var requestBody = readJson(new PostUpdateRequest("수정 포스트"));

		Mockito.when(postService.updatePost(Mockito.anyLong(), Mockito.any()))
			.thenReturn(new Post(1L, "수정 포스트", ZonedDateTime.now()));

		mockMvc.perform(
			MockMvcRequestBuilders.patch(GET_POST_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(requestBody)
		).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Post 수정 실패")
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
	}

	@Test
	@DisplayName("Post 삭제 성공")
	void deletePostOk() throws Exception {
		Mockito.doNothing().when(postService).deletePost(Mockito.anyLong());

		mockMvc.perform(
				MockMvcRequestBuilders.delete(GET_POST_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
			).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DisplayName("Post 삭제 실패")
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
	}

}