package com.imwoo.threads.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.imwoo.threads.model.Post;

class PostServiceTest {

	private final PostService postService = new PostService();

	@Test
	@DisplayName("다건 Post 서비스 조회")
	void getPosts() {
		List<Post> posts = postService.getPosts();
		// null 확인
		assertThat(posts).isNotNull();
		// 조회 건수 확인
		assertThat(posts.size()).isGreaterThan(2);
	}

	@Test
	@DisplayName("단건 Post 서비스 조회 존재 O")
	void getPostByPostIdExists() {
		Optional<Post> post = postService.getPostByPostId(1L);
		assertThat(post).isPresent();
	}

	@Test
	@DisplayName("단건 Post 서비스 조회 존재 X")
	void getPostByPostIdNonExists() {
		Optional<Post> post = postService.getPostByPostId(999L);
		assertThat(post.isPresent()).isFalse();
	}

}