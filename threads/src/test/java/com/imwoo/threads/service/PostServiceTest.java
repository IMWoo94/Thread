package com.imwoo.threads.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imwoo.threads.exception.post.PostCreatedFailureException;
import com.imwoo.threads.exception.post.PostNotFoundException;
import com.imwoo.threads.exception.user.UserNotAllowedException;
import com.imwoo.threads.model.entity.PostEntity;
import com.imwoo.threads.model.entity.UserEntity;
import com.imwoo.threads.model.post.request.PostCreateRequest;
import com.imwoo.threads.model.post.request.PostUpdateRequest;
import com.imwoo.threads.repository.PostEntityRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	private final static UserEntity AUTHORIZED_USER = new UserEntity(1L, "admin", "admin", null, null,
		ZonedDateTime.now(),
		ZonedDateTime.now(),
		null);
	private final static UserEntity OTHER_USER = new UserEntity(2L, "other", "other", null, null,
		ZonedDateTime.now(),
		ZonedDateTime.now(),
		null);
	@InjectMocks
	private PostService postService;
	@Mock
	private PostEntityRepository postEntityRepository;

	@Test
	@DisplayName("[Success] 전체 Post 조회 서비스 테스트")
	void getMultiPostServiceTestSuccess() {
		// given
		var postEntities = new ArrayList<PostEntity>();

		// mocking
		when(postEntityRepository.findAll())
			.thenReturn(postEntities);

		// when
		postService.getPosts();

		// then
		verify(postEntityRepository, only()).findAll();
		verify(postEntityRepository, timeout(3000)).findAll();

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Success] 단건 Post 조회 서비스 테스트")
	void getSinglePostServiceTestSuccess() {
		// given
		var postEntity = PostEntity.of("", AUTHORIZED_USER);
		var postId = anyLong();

		// mocking
		when(postEntityRepository.findById(postId))
			.thenReturn(Optional.of(postEntity));

		// when
		postService.getPostByPostId(postId);

		// then
		verify(postEntityRepository, only()).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Failure] 단건 Post 조회 서비스 테스트")
	void getSinglePostServiceTestFailure() {
		// given
		var postId = anyLong();

		// mocking
		when(postEntityRepository.findById(postId))
			.thenThrow(new PostNotFoundException(postId));

		// when

		// then
		assertThatThrownBy(() -> postService.getPostByPostId(anyLong()))
			.isInstanceOf(PostNotFoundException.class);

		verify(postEntityRepository, only()).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Success] Post 신규 생성 서비스 테스트")
	void newCreatePostServiceTestSuccess() {
		// given
		var body = "new created post test body";
		var postEntity = PostEntity.of(body, AUTHORIZED_USER);

		// mocking
		when(postEntityRepository.save(any(PostEntity.class)))
			.thenReturn(postEntity);

		// when
		var newPost = postService.createPost(new PostCreateRequest(body), AUTHORIZED_USER);

		// then
		assertThat(newPost).isNotNull();
		assertThat(newPost.body()).isEqualTo(body);

		verify(postEntityRepository, only()).save(any(PostEntity.class));
		verify(postEntityRepository, timeout(3000)).save(any(PostEntity.class));

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Failure] Post 신규 생성 서비스 테스트")
	void newCreatePostServiceTestFailure() {
		// given
		var body = "new created post test body";

		// mocking
		when(postEntityRepository.save(any(PostEntity.class)))
			.thenThrow(new PostCreatedFailureException(new RuntimeException()));

		// when

		// then
		assertThatThrownBy(() -> postService.createPost(new PostCreateRequest(body), null))
			.isInstanceOf(PostCreatedFailureException.class);

		verify(postEntityRepository, only()).save(any(PostEntity.class));
		verify(postEntityRepository, timeout(3000)).save(any(PostEntity.class));

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Success] Post 수정 서비스 테스트")
	void updatedPostServiceTestSuccess() {

		// given
		var body = "modified post test body";
		var mockTestPost = Optional.of(PostEntity.of(body, AUTHORIZED_USER));

		// mocking
		when(postEntityRepository.findById(anyLong()))
			.thenReturn(mockTestPost);
		when(postEntityRepository.save(any(PostEntity.class)))
			.thenReturn(mockTestPost.get());

		// when
		var updatePost = postService.updatePost(anyLong(), new PostUpdateRequest(body), AUTHORIZED_USER);

		// then
		assertThat(updatePost).isNotNull();
		assertThat(updatePost.body()).isEqualTo(body);

		verify(postEntityRepository, times(1)).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verify(postEntityRepository, times(1)).save(any(PostEntity.class));
		verify(postEntityRepository, timeout(3000)).save(any(PostEntity.class));

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Failure] Post 수정 서비스 테스트")
	void updatedPostServiceTestFailure() {
		// given
		var body = "modified post test body";

		// mocking
		when(postEntityRepository.findById(anyLong()))
			.thenThrow(new PostNotFoundException());

		// when

		// then
		assertThatThrownBy(() -> postService.updatePost(anyLong(), new PostUpdateRequest(body), AUTHORIZED_USER))
			.isInstanceOf(PostNotFoundException.class);

		verify(postEntityRepository, only()).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Failure] Post 수정 서비스 권한 불일치 테스트")
	void updatedPostServiceNotAllowedTestFailure() {

		// given
		var body = "modified post test body";
		var mockTestPost = Optional.of(PostEntity.of(body, AUTHORIZED_USER));

		// mocking
		when(postEntityRepository.findById(anyLong()))
			.thenReturn(mockTestPost);

		// when
		assertThatThrownBy(() -> postService.updatePost(anyLong(), new PostUpdateRequest(body), OTHER_USER))
			.isInstanceOf(UserNotAllowedException.class);

		// then
		verify(postEntityRepository, only()).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Success] Post 삭제 서비스 테스트")
	void deletedPostServiceTestSuccess() {
		// given
		var deleteDateTime = ZonedDateTime.now();
		var findPostEntity = Optional.of(PostEntity.of("", AUTHORIZED_USER));

		// mocking
		when(postEntityRepository.findById(anyLong()))
			.thenReturn(findPostEntity);
		doAnswer(invocationOnMock -> {
			PostEntity entity = invocationOnMock.getArgument(0);

			entity.setDeletedDateTime(deleteDateTime);

			assertThat(entity.getDeletedDateTime()).isNotNull();
			assertThat(entity.getDeletedDateTime()).isEqualTo(deleteDateTime);

			return null;
		}).when(postEntityRepository).delete(any(PostEntity.class));

		// when
		postService.deletePost(anyLong(), AUTHORIZED_USER);

		// then
		verify(postEntityRepository, times(1)).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verify(postEntityRepository, times(1)).delete(any(PostEntity.class));
		verify(postEntityRepository, timeout(3000)).delete(any(PostEntity.class));
		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Failure] Post 삭제 서비스 테스트")
	void deletedPostServiceTestFailure() {
		// given

		// mocking
		when(postEntityRepository.findById(anyLong()))
			.thenThrow(new PostNotFoundException());

		// when

		// then
		assertThatThrownBy(() -> postService.deletePost(anyLong(), AUTHORIZED_USER))
			.isInstanceOf(PostNotFoundException.class);

		verify(postEntityRepository, only()).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verifyNoMoreInteractions(postEntityRepository);
	}

	@Test
	@DisplayName("[Failure] Post 삭제 서비스 권한 불일치 테스트")
	void deletedPostServiceNotAllowedTestFailure() {
		// given
		var findPostEntity = Optional.of(PostEntity.of("", AUTHORIZED_USER));

		// mocking
		when(postEntityRepository.findById(anyLong()))
			.thenReturn(findPostEntity);

		// when
		assertThatThrownBy(() -> postService.deletePost(anyLong(), OTHER_USER))
			.isInstanceOf(UserNotAllowedException.class);

		// then
		verify(postEntityRepository, only()).findById(anyLong());
		verify(postEntityRepository, timeout(3000)).findById(anyLong());

		verify(postEntityRepository, times(0)).delete(any(PostEntity.class));
		verifyNoMoreInteractions(postEntityRepository);
	}

}