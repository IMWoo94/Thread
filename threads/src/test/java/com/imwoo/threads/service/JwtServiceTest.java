package com.imwoo.threads.service;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;
	@Mock
	private UserDetails mockUserDetails;

	@BeforeEach
	void setUp() throws Exception {
		lenient().when(mockUserDetails.getUsername()).thenReturn("testUser");
	}

	@Test
	@DisplayName("[Success] JWT 생성 서비스 테스트")
	void newJwtCreatedServiceTestSuccess() {
		// given
		String accessToken;

		// mocking

		// when
		accessToken = jwtService.generateAccessToken(mockUserDetails);

		// then
		Assertions.assertThat(accessToken).isNotBlank();

		verify(mockUserDetails, times(1)).getUsername();
	}

	@Test
	@DisplayName("[Success] JWT 검증 서비스 테스트")
	void getUsernameJwtValidServiceTestSuccess() {
		// given
		String accessToken = jwtService.generateAccessToken(mockUserDetails);
		var username = "testUser";

		// mocking

		// when
		var getUsername = jwtService.getUsername(accessToken);

		// then
		Assertions.assertThat(username).isEqualTo(getUsername);

		verify(mockUserDetails, times(1)).getUsername();
	}

	@Test
	@DisplayName("[Failure] JWT 검증 Invalid 서비스 테스트")
	void getUsernameJwtInvalidServiceTestFailure() {
		// given
		String invalidToken = "invalidToken";

		// mocking

		// when

		// then
		Assertions.assertThatThrownBy(() -> jwtService.getUsername(invalidToken))
			.isInstanceOf(JwtException.class);
	}

}