package com.imwoo.threads.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.imwoo.threads.exception.post.PostCreatedFailureException;
import com.imwoo.threads.exception.post.PostNotFoundException;
import com.imwoo.threads.exception.user.UserNotAllowedException;
import com.imwoo.threads.model.entity.PostEntity;
import com.imwoo.threads.model.entity.UserEntity;
import com.imwoo.threads.model.post.request.PostCreateRequest;
import com.imwoo.threads.model.post.request.PostUpdateRequest;
import com.imwoo.threads.model.post.response.PostResponse;
import com.imwoo.threads.repository.PostEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostEntityRepository postEntityRepository;

	// 전체 조회
	// TODO 페이지 처리 필요
	public List<PostResponse> getPosts() {
		return postEntityRepository.findAll()
			.stream().map(PostResponse::from).toList();
	}

	// 단건 조회
	public PostResponse getPostByPostId(Long postId) {
		var postEntity = getPostByPostIdWithThrow(postId);
		return PostResponse.from(postEntity);
	}

	// 생성
	public PostResponse createPost(PostCreateRequest postCreateRequest, UserEntity userEntity) {
		try {
			var postEntity = PostEntity.of(postCreateRequest.body(), userEntity);
			postEntityRepository.save(postEntity);

			return PostResponse.from(postEntity);
		} catch (RuntimeException e) {
			// 예외 전환
			throw new PostCreatedFailureException(e);
		}
	}

	// 수정
	public PostResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest, UserEntity userEntity) {
		var postEntity = getPostByPostIdWithThrow(postId);

		// 작성자 확인 후 Update 진행
		// TODO : 관리자 권한을 가진 사용자라면 허용 분기 등 처리 해보기
		if (!postEntity.getUser().equals(userEntity)) {
			throw new UserNotAllowedException();
		}

		// TODO 트랜잭션 사용해서 JPA 영속성 컨텍스트의 더티 체킹 활용 해보기. / 현재는 Merge 방식으로 적용
		postEntity.setBody(postUpdateRequest.body());
		postEntityRepository.save(postEntity);

		return PostResponse.from(postEntity);
	}

	// 삭제
	public void deletePost(Long postId, UserEntity userEntity) {
		var postEntity = getPostByPostIdWithThrow(postId);

		// 작성자 확인 후 Delete 진행
		// TODO : 관리자 권한을 가진 사용자라면 허용 분기 등 처리 해보기
		if (!postEntity.getUser().equals(userEntity)) {
			throw new UserNotAllowedException();
		}

		postEntityRepository.delete(postEntity);
	}

	// postId 로 검색
	private PostEntity getPostByPostIdWithThrow(Long postId) {
		return postEntityRepository.findById(postId)
			.orElseThrow(() -> new PostNotFoundException(postId));
	}
}
