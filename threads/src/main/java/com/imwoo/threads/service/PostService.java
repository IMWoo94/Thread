package com.imwoo.threads.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.imwoo.threads.model.Post;

@Service
public class PostService {

	// Controller 중복 부분 임시 Static 처리
	private static final List<Post> posts = new ArrayList<>();

	// static 블럭을 통해서 인스턴스 생성 시 한번만 동작하도록
	static {
		posts.add(new Post(1L, "Post 1", ZonedDateTime.now()));
		posts.add(new Post(2L, "Post 2", ZonedDateTime.now()));
		posts.add(new Post(3L, "Post 3", ZonedDateTime.now()));
	}

	public List<Post> getPosts() {
		// TODO 차후 DB 정보 기반
		return posts;
	}

	public Optional<Post> getPostByPostId(Long postId) {
		return posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();
	}

}
