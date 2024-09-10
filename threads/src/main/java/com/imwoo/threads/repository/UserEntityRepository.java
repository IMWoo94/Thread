package com.imwoo.threads.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imwoo.threads.model.entity.UserEntity;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

	// Containing : %param%
	List<UserEntity> findByUsernameContaining(String username);

	Optional<UserEntity> findByUsername(String username);
}
