package com.imwoo.threads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imwoo.threads.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
