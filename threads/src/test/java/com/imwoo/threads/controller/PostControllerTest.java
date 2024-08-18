package com.imwoo.threads.controller;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.imwoo.threads.model.Post;
import com.imwoo.threads.service.PostService;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(PostController.class)
@Slf4j
class PostControllerTest {

	private final String GET_POSTS_URL = "/api/v1/posts";
	private final String GET_POST_URL = "/api/v1/posts/1";
	@MockBean
	private PostService postService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Post 다건 조회")
	void getPostByPostsId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(GET_POSTS_URL))
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

}