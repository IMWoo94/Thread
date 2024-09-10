package com.imwoo.threads.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imwoo.threads.model.entity.PostEntity;
import com.imwoo.threads.model.entity.UserEntity;

public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

	List<PostEntity> findByUser(UserEntity user);
}
