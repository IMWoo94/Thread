package com.imwoo.threads.model.user.request;

import jakarta.validation.constraints.NotEmpty;

public record UserAuthenticateRequest(
	@NotEmpty String username,
	@NotEmpty String password
) {
}
