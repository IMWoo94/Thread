package com.imwoo.threads.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imwoo.threads.model.entity.PostEntity;
import com.imwoo.threads.model.post.response.PostResponse;

public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

	List<PostResponse> findAllPostResponseBy();

}
