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

import com.imwoo.threads.model.PostCreateRequest;
import com.imwoo.threads.model.PostResponse;
import com.imwoo.threads.model.PostUpdateRequest;
import com.imwoo.threads.model.entity.PostEntity;
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

	@Test
	@DisplayName("Post 생성 테스트")
	void createPost() {
		// given
		var body = "신규 포스트 생성";
		var postRequest = new PostCreateRequest(body);

		// mocking
		when(postEntityRepository.save(any(PostEntity.class)))
			.thenReturn(new PostEntity(1L, body, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// when
		PostResponse post = postService.createPost(postRequest);

		// then
		assertThat(post).isNotNull();
		assertThat(post.postId()).isGreaterThan(0);
		assertThat(post.body()).isEqualTo(body);
	}

	@Test
	@DisplayName("Post 수정 성공 테스트")
	void updatePostOk() {

		// given
		var body = "포스트 내용 변경";
		var postRequest = new PostUpdateRequest(body);

		Optional<PostEntity> mockTestPost = Optional.of(
			new PostEntity(1L, "mock test body", ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(postEntityRepository.findById(anyLong())).thenReturn(mockTestPost);
		when(postEntityRepository.save(any(PostEntity.class))).thenReturn(mockTestPost.get());

		// when
		postService.updatePost(mockTestPost.get().getPostId(), postRequest);

		// then
		Mockito.verify(postEntityRepository, times(1)).findById(anyLong());
		Mockito.verify(postEntityRepository, times(1)).save(any(PostEntity.class));
		Mockito.verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("Post 수정 실패 테스트")
	void updatePostFail() {
		// given
		// null

		// mocking
		when(postEntityRepository.findById(anyLong())).thenThrow(
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

		// when

		// then
		assertThatThrownBy(() -> postService.updatePost(anyLong(), new PostUpdateRequest("mock test body")))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessage("%s %s", HttpStatus.NOT_FOUND, "\"Post not found.\"");

		Mockito.verify(postEntityRepository, times(1)).findById(anyLong());
		Mockito.verify(postEntityRepository, only()).findById(anyLong());
		Mockito.verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("Post 삭제 성공 테스트")
	void deletePostOk() {
		// given
		Optional<PostEntity> mockTestPost = Optional.of(
			new PostEntity(1L, "mock test body", ZonedDateTime.now(), ZonedDateTime.now(), null));

		var nowDateTime = ZonedDateTime.now();
		// mocking
		when(postEntityRepository.findById(anyLong())).thenReturn(mockTestPost);
		doAnswer(invocationOnMock -> {
			mockTestPost.get().setDeletedDateTime(nowDateTime);
			return null;
		}).when(postEntityRepository).delete(any(PostEntity.class));

		// when
		postService.deletePost(mockTestPost.get().getPostId());

		// then
		assertThat(mockTestPost.get().getDeletedDateTime()).isNotNull();
		assertThat(mockTestPost.get().getDeletedDateTime()).isEqualTo(nowDateTime);
		Mockito.verify(postEntityRepository, times(1)).findById(anyLong());
		Mockito.verify(postEntityRepository, times(1)).delete(any(PostEntity.class));
		Mockito.verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("Post 삭제 실패 테스트")
	void deletePostFail() {
		// given
		// null

		// mocking
		when(postEntityRepository.findById(anyLong())).thenThrow(
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

		// when

		// then
		assertThatThrownBy(() -> postService.deletePost(anyLong()))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessage("%s %s", HttpStatus.NOT_FOUND, "\"Post not found.\"");

		Mockito.verify(postEntityRepository, times(1)).findById(anyLong());
		Mockito.verify(postEntityRepository, only()).findById(anyLong());
		Mockito.verifyNoMoreInteractions(postEntityRepository);
	}

}