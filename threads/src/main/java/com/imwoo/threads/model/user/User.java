package com.imwoo.threads.model.user;

import java.time.ZonedDateTime;

import com.imwoo.threads.model.entity.UserEntity;

public record User(
	Long userId,
	String username,
	String profile,
	String description,
	ZonedDateTime createdDateTime,
	ZonedDateTime updatedDateTime
) {

	public static User from(UserEntity userEntity) {
		return new User(
			userEntity.getUserId(),
			userEntity.getUsername(),
			userEntity.getProfile(),
			userEntity.getDescription(),
			userEntity.getCreatedDateTime(),
			userEntity.getUpdatedDateTime()
		);
	}
}
