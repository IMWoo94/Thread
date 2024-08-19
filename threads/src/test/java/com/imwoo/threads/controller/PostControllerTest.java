package com.imwoo.threads.controller;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imwoo.threads.model.Post;
import com.imwoo.threads.model.PostCreateRequest;
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

}