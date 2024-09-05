package com.imwoo.threads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imwoo.threads.model.entity.PostEntity;

public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {
}
