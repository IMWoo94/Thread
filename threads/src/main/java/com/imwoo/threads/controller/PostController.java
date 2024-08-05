package com.imwoo.threads.controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.imwoo.threads.model.Post;

@RestController
public class PostController {

	@GetMapping("/api/v1/posts")
	public ResponseEntity<List<Post>> getPosts() {
		// TODO 실제 포스트 정보 DB 조회 처리
		ArrayList<Post> posts = new ArrayList<>();
		posts.add(new Post(1L, "Post 1", ZonedDateTime.now()));
		posts.add(new Post(2L, "Post 2", ZonedDateTime.now()));
		posts.add(new Post(3L, "Post 3", ZonedDateTime.now()));

		return new ResponseEntity<>(posts, HttpStatus.OK);
	}

	@GetMapping("/api/v1/posts/{postId}")
	public ResponseEntity<Post> getPost(
		@PathVariable("postId") Long postId
	) {
		// TODO 실제 포스트 정보 DB 조회 처리
		ArrayList<Post> posts = new ArrayList<>();
		posts.add(new Post(1L, "Post 1", ZonedDateTime.now()));
		posts.add(new Post(2L, "Post 2", ZonedDateTime.now()));
		posts.add(new Post(3L, "Post 3", ZonedDateTime.now()));

		Optional<Post> matchingPost = posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();

		return matchingPost
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> getException(RuntimeException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
