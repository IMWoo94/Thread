package com.imwoo.threads.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.imwoo.threads.model.PostResponse;
import com.imwoo.threads.repository.PostEntityRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	private PostService postService;
	@Mock
	private PostEntityRepository postEntityRepository;

	@Test
	@DisplayName("다건 Post 서비스 조회")
	void getPosts() {
		// given
		// null

		// mocking
		when(postEntityRepository.findAllPostResponseBy())
			.thenReturn(
				List.of(
					new PostResponse(
						1L,
						"mock test body",
						ZonedDateTime.now(),
						ZonedDateTime.now(),
						null
					)
				)
			);

		// when
		List<PostResponse> posts = postService.getPosts();

		// then
		// null 확인
		assertThat(posts).isNotNull();
		// 조회 건수 확인
		assertThat(posts.size()).isGreaterThan(0);

		Mockito.verify(postEntityRepository, times(1)).findAllPostResponseBy();
		Mockito.verify(postEntityRepository, only()).findAllPostResponseBy();
		Mockito.verify(postEntityRepository, timeout(3000)).findAllPostResponseBy();

		Mockito.verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("단건 Post 서비스 조회 존재 O")
	void getPostByPostIdExists() {
		// given

		// mocking
		when(postEntityRepository.findByPostId(anyLong()))
			.thenReturn(
				Optional.of(new PostResponse(1L, "mock test body", ZonedDateTime.now(), ZonedDateTime.now(), null))
			);

		// when
		PostResponse post = postService.getPostByPostId(anyLong());

		// then
		assertThat(post).isNotNull();
		assertThat(post.postId()).isEqualTo(1L);

		Mockito.verify(postEntityRepository, times(1)).findByPostId(anyLong());
		Mockito.verify(postEntityRepository, only()).findByPostId(anyLong());
		Mockito.verify(postEntityRepository, timeout(3000)).findByPostId(anyLong());

		Mockito.verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("단건 Post 서비스 조회 존재 X")
	void getPostByPostIdNonExists() {
		// given
		// null

		// mocking
		when(postEntityRepository.findByPostId(anyLong())).thenThrow(
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

		// when

		// then
		assertThatThrownBy(() -> postService.getPostByPostId(anyLong()))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessage("%s %s", HttpStatus.NOT_FOUND, "\"Post not found.\"");

		Mockito.verify(postEntityRepository, times(1)).findByPostId(anyLong());
		Mockito.verify(postEntityRepository, only()).findByPostId(anyLong());
		Mockito.verify(postEntityRepository, timeout(3000)).findByPostId(anyLong());

		Mockito.verifyNoMoreInteractions(postEntityRepository);
	}

	// @Test
	// @DisplayName("Post 생성 테스트")
	// void createPost() {
	// 	var body = "신규 포스트 생성";
	// 	Post newPost = postService.createPost(new PostCreateRequest(body));
	//
	// 	assertThat(newPost).isNotNull();
	// 	assertThat(newPost.getPostId()).isGreaterThan(0);
	// 	assertThat(newPost.getBody()).isEqualTo(body);
	// }
	//
	// @Test
	// @DisplayName("Post 수정 성공 테스트")
	// void updatePostOk() {
	// 	var post = postService.getPostByPostId(1L);
	// 	var updateBody = "body update context";
	//
	// 	Post updatePost = postService.updatePost(post.get().getPostId(), new PostUpdateRequest(updateBody));
	//
	// 	assertThat(updatePost.getBody()).isEqualTo(updateBody);
	// }
	//
	// @Test
	// @DisplayName("Post 수정 실패 테스트")
	// void updatePostFail() {
	// 	assertThatThrownBy(() -> postService.updatePost(999L, new PostUpdateRequest("body update context")))
	// 		.isInstanceOf(ResponseStatusException.class)
	// 		.hasMessage("%s %s", HttpStatus.NOT_FOUND, "\"Post not found.\"");
	// }
	//
	// @Test
	// @DisplayName("Post 삭제 성공 테스트")
	// void deletePostOk() {
	// 	var post = postService.getPostByPostId(1L);
	// 	postService.deletePost(post.get().getPostId());
	//
	// 	assertThat(postService.getPostByPostId(1L)).isEmpty();
	// }
	//
	// @Test
	// @DisplayName("Post 삭제 실패 테스트")
	// void deletePostFail() {
	// 	assertThatThrownBy(() -> postService.deletePost(999L))
	// 		.isInstanceOf(ResponseStatusException.class)
	// 		.hasMessage("%s %s", HttpStatus.NOT_FOUND, "\"Post not found.\"");
	// }

}