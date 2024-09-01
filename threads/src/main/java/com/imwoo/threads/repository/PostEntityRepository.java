package com.imwoo.threads.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imwoo.threads.model.PostResponse;
import com.imwoo.threads.model.entity.PostEntity;

public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

	List<PostResponse> findAllPostResponseBy();

}
