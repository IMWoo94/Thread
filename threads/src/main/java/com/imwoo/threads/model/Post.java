package com.imwoo.threads.model;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Post {
	private Long postId;
	private String body;
	private ZonedDateTime createdDateTime;

}
