package com.imwoo.threads.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imwoo.threads.model.Post;
import com.imwoo.threads.model.PostCreateRequest;
import com.imwoo.threads.model.PostResponse;
import com.imwoo.threads.model.PostUpdateRequest;
import com.imwoo.threads.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {

	private final PostService postService;

	@GetMapping
	public ResponseEntity<List<PostResponse>> getPosts() {
		return ResponseEntity.ok(postService.getPosts());
	}

	@GetMapping("{postId}")
	public ResponseEntity<PostResponse> getPostByPostId(
		@PathVariable("postId") Long postId
	) {
		return ResponseEntity.ok(postService.getPostByPostId(postId));
	}

	@PostMapping
	public ResponseEntity<PostResponse> createPost(
		@RequestBody PostCreateRequest postCreateRequest) {
		// Post 신규 생성
		log.info("postController create before : {}", postCreateRequest.toString());
		var post = postService.createPost(postCreateRequest);
		log.info("postController create after : {}", post.toString());
		return ResponseEntity.ok(post);
	}

	@PatchMapping("/{postId}")
	public ResponseEntity<Post> updatePost(
		@PathVariable Long postId,
		@RequestBody PostUpdateRequest postUpdateRequest
	) {
		// Post 수정
		log.info("postController update before : {}", postUpdateRequest.toString());
		var post = postService.updatePost(postId, postUpdateRequest);
		log.info("postController update after : {}", post.toString());
		return ResponseEntity.ok(post);
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> deletePost(
		@PathVariable Long postId
	) {
		// Post 삭제
		postService.deletePost(postId);
		// NO_CONTENT(204, HttpStatus.Series.SUCCESSFUL, "No Content")
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> getException(RuntimeException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
