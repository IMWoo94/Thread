package com.imwoo.threads.controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
