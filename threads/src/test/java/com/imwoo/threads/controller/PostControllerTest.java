package com.imwoo.threads.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(PostController.class)
class PostControllerTest {

	private final String GET_POSTS_URL = "/api/v1/posts";
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Post 다건 조회")
	void getPosts() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(GET_POSTS_URL))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType("application/json"));
	}

	@Test
	@DisplayName("Post 단건 조회")
	void getPost() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(GET_POSTS_URL + "/1"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType("application/json"));
	}

}