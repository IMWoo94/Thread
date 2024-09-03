package com.imwoo.threads.model;

import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.imwoo.threads.model.post.Post;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class PostTest {

	@Test
	@DisplayName("Post 객체 생성 테스트")
	void createdPost() {
		Post post = new Post();
		assertThat(post).isNotNull();
		Post post1 = new Post(1L, "내용1", ZonedDateTime.now());
		assertThat(post1).isNotNull();
	}

	@Test
	@DisplayName("Post 객체 동일성 테스트")
	void postEquals() {
		Post postA = new Post(1L, "내용1", ZonedDateTime.now());
		Post postB = new Post(1L, "내용1", ZonedDateTime.now());
		assertThat(postA).isEqualTo(postB);
		assertThat(postA.hashCode()).isEqualTo(postB.hashCode());
	}
}