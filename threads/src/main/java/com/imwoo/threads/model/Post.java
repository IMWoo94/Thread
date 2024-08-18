package com.imwoo.threads.model;

import java.time.ZonedDateTime;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
	private Long postId;
	private String body;
	private ZonedDateTime createdDateTime;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Post post = (Post)o;
		return Objects.equals(getPostId(), post.getPostId()) && Objects.equals(getBody(),
			post.getBody());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPostId(), getBody());
	}

	@Override
	public String toString() {
		return "Post{" +
			"postId=" + postId +
			", body='" + body + '\'' +
			", createdDateTime=" + createdDateTime +
			'}';
	}
}
