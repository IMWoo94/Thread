package com.imwoo.threads.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponse(
	Long postId,
	String body,
	ZonedDateTime createdDateTime,
	ZonedDateTime updatedDateTime,
	ZonedDateTime deletedDateTime
) {
}
