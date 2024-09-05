package com.imwoo.threads.model.post.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.imwoo.threads.model.entity.PostEntity;
import com.imwoo.threads.model.user.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponse(
	Long postId,
	String body,
	User user,
	ZonedDateTime createdDateTime,
	ZonedDateTime updatedDateTime,
	ZonedDateTime deletedDateTime
) {

	public static PostResponse from(PostEntity postEntity) {
		return new PostResponse(
			postEntity.getPostId(),
			postEntity.getBody(),
			User.from(postEntity.getUser()),
			postEntity.getCreatedDateTime(),
			postEntity.getUpdatedDateTime(),
			postEntity.getDeletedDateTime()
		);
	}
}
