package com.imwoo.threads.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.imwoo.threads.model.Post;
import com.imwoo.threads.model.PostCreateRequest;
import com.imwoo.threads.model.PostResponse;
import com.imwoo.threads.model.PostUpdateRequest;
import com.imwoo.threads.model.entity.PostEntity;
import com.imwoo.threads.repository.PostEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	// Controller 중복 부분 임시 Static 처리
	private static final List<Post> posts = new ArrayList<>();

	// static 블럭을 통해서 인스턴스 생성 시 한번만 동작하도록
	static {
		posts.add(new Post(1L, "Post 1", ZonedDateTime.now()));
		posts.add(new Post(2L, "Post 2", ZonedDateTime.now()));
		posts.add(new Post(3L, "Post 3", ZonedDateTime.now()));
	}

	private final PostEntityRepository postEntityRepository;

	public List<PostResponse> getPosts() {
		return postEntityRepository.findAllPostResponseBy();
	}

	public PostResponse getPostByPostId(Long postId) {
		return postEntityRepository.findByPostId(postId)
			.orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Post not found."));
	}

	public Optional<Post> getPost(Long postId) {
		return posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();
	}

	public PostResponse createPost(PostCreateRequest postCreateRequest) {
		var postEntity = new PostEntity();
		postEntity.setBody(postCreateRequest.body());

		var savePostEntity = postEntityRepository.save(postEntity);
		return PostResponse.from(savePostEntity);
	}

	public Post updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
		Optional<Post> findPost = getPost(postId);

		findPost.ifPresentOrElse(
			post -> post.setBody(postUpdateRequest.body()),
			() -> {
				throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Post not found.");
			});

		return findPost.get();
	}

	public void deletePost(Long postId) {
		Optional<Post> findPost = getPost(postId);

		findPost.ifPresentOrElse(
			posts::remove,
			() -> {
				throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Post not found.");
			});

	}
}
