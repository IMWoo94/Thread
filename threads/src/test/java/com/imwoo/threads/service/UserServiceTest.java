package com.imwoo.threads.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.imwoo.threads.exception.user.UserDuplicatedException;
import com.imwoo.threads.exception.user.UserNotFoundException;
import com.imwoo.threads.model.entity.UserEntity;
import com.imwoo.threads.model.user.User;
import com.imwoo.threads.repository.UserEntityRepository;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserServiceTest {

	@InjectMocks
	private UserService userService;
	@Mock
	private JwtService jwtService;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	@Mock
	private UserEntityRepository userEntityRepository;

	@Test
	@DisplayName("[Success] User 조회 서비스 테스트")
	void loadUserByUsernameServiceTestSuccess() {
		// given
		var username = "admin";

		// mocking
		Mockito.when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(new UserEntity()));

		// when
		var result = userService.loadUserByUsername(username);

		// then
		Assertions.assertThat(result).isNotNull();

		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, only()).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());

		verifyNoMoreInteractions(userEntityRepository);
	}

	@Test
	@DisplayName("[Failure] User 조회 Not Found 서비스 테스트")
	void loadUserByUsernameServiceTestFailure() {
		// given
		var username = "admin";

		// mocking
		Mockito.when(userEntityRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException(username));

		// when

		// then
		Assertions.assertThatThrownBy(() -> userService.loadUserByUsername(username))
			.isInstanceOf(UserNotFoundException.class);

		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, only()).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());

		verifyNoMoreInteractions(userEntityRepository);
	}

	@Test
	@DisplayName("[Success] 회원 가입 서비스 테스트")
	void signUpServiceTestSuccess() {
		// given
		var username = "admin";
		var password = "admin";
		var encodePassword = passwordEncoder.encode(password);

		// mocking
		when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		when(userEntityRepository.save(any(UserEntity.class))).thenReturn(
			new UserEntity(1L, username, encodePassword, null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// when
		var result = userService.signUp(username, password);

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result).isInstanceOf(User.class);

		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());
		verify(userEntityRepository, times(1)).save(any(UserEntity.class));
		verify(userEntityRepository, timeout(3000)).save(any(UserEntity.class));

		verifyNoMoreInteractions(userEntityRepository);

	}

	@Test
	@DisplayName("[Failure] 회원 가입 서비스 중복 테스트")
	void signUpServiceTestDuplicateFailure() {
		// given
		var username = "duplicate";
		var password = "duplicate";

		// mocking
		when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(new UserEntity()));

		// when

		// then
		Assertions.assertThatThrownBy(() -> userService.signUp(username, password))
			.isInstanceOf(UserDuplicatedException.class);

		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());
		verify(userEntityRepository, times(0)).save(any(UserEntity.class));

		verifyNoMoreInteractions(userEntityRepository);

	}

	@Test
	@DisplayName("[Success] 회원 인증 서비스 테스트")
	void authenticateServiceTestSuccess() {
		// given
		var username = "admin";
		var password = "admin";
		var encodePassword = new BCryptPasswordEncoder().encode(password);
		var findUserEntity = Optional.of(
			new UserEntity(1L, username, encodePassword, null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(userEntityRepository.findByUsername(anyString())).thenReturn(findUserEntity);
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtService.generateAccessToken(findUserEntity.get())).thenReturn("access_token");

		// when
		var result = userService.authenticate(username, password);

		// then
		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());
		verify(passwordEncoder, times(1)).matches(anyString(), anyString());
		verify(jwtService, times(1)).generateAccessToken(any());

		verifyNoMoreInteractions(userEntityRepository);
	}

	/**
	 * 회원 인증 서비스 실패 케이스
	 * 1. username UserNotFoundException Error
	 * 2. password 불일치 케이스
	 */
	@Test
	@DisplayName("[Failure] 회원 인증 서비스 Not Found 테스트")
	void authenticateServiceNotFoundTestFailure() {
		// given
		var username = "null";
		var password = "admin";

		// mocking
		Mockito.when(userEntityRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException(username));

		// when

		// then
		Assertions.assertThatThrownBy(() -> userService.authenticate(username, password))
			.isInstanceOf(UserNotFoundException.class);

		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, only()).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());

		verifyNoMoreInteractions(userEntityRepository);

	}

	@Test
	@DisplayName("[Failure] 회원 인증 서비 패스워드 불일치 테스트")
	void authenticateServicePasswordNotMatchedTestFailure() {
		// given
		var username = "null";
		var password = "admin";
		var encodePassword = new BCryptPasswordEncoder().encode(password);
		var findUserEntity = Optional.of(
			new UserEntity(1L, username, encodePassword, null, null, ZonedDateTime.now(), ZonedDateTime.now(), null));

		// mocking
		when(userEntityRepository.findByUsername(anyString())).thenReturn(findUserEntity);
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		// when
		Assertions.assertThatThrownBy(() -> userService.authenticate(username, password))
			.isInstanceOf(UserNotFoundException.class);

		// then
		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());
		verify(passwordEncoder, times(1)).matches(anyString(), anyString());
		verify(jwtService, times(0)).generateAccessToken(any());

		verifyNoMoreInteractions(userEntityRepository);

	}

	@Test
	@DisplayName("[Success] User 조회 전체 서비스 테스트")
	void userSearchQueryNotExistsServiceTestSuccess() {
		// given

		// mocking
		Mockito.when(userEntityRepository.findAll()).thenReturn(List.of(new UserEntity()));

		// when
		userService.getUsers(null);

		// then
		verify(userEntityRepository, times(1)).findAll();
		verify(userEntityRepository, only()).findAll();
		verify(userEntityRepository, timeout(3000)).findAll();

		verifyNoMoreInteractions(userEntityRepository);
	}

	@Test
	@DisplayName("[Success] User 조회 Like 서비스 테스트")
	void userSearchQueryExistsServiceTestSuccess() {
		// given
		var query = "a";

		// mocking
		Mockito.when(userEntityRepository.findByUsernameContaining(anyString())).thenReturn(List.of(new UserEntity()));

		// when
		userService.getUsers(query);

		// then
		verify(userEntityRepository, times(1)).findByUsernameContaining(anyString());
		verify(userEntityRepository, only()).findByUsernameContaining(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsernameContaining(anyString());

		verifyNoMoreInteractions(userEntityRepository);
	}

	@Test
	@DisplayName("[Success] User 조회 단건 서비스 테스트")
	void userSearchUsernameServiceTestSuccess() {
		// given
		var username = "admin";

		// mocking
		Mockito.when(userEntityRepository.findByUsername(anyString()))
			.thenReturn(Optional.of(
				new UserEntity(1L, username, "admin", null, null, ZonedDateTime.now(), ZonedDateTime.now(), null)));

		// when
		userService.getUser(username);

		// then
		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, only()).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());

		verifyNoMoreInteractions(userEntityRepository);
	}

	@Test
	@DisplayName("[Failure] User 조회 단건 서비스 테스트")
	void userSearchUsernameServiceTestFailure() {
		// given
		var username = "admin";

		// mocking
		Mockito.when(userEntityRepository.findByUsername(anyString()))
			.thenReturn(Optional.empty());

		// when
		Assertions.assertThatThrownBy(() -> userService.getUser(username))
			.isInstanceOf(UserNotFoundException.class);

		// then
		verify(userEntityRepository, times(1)).findByUsername(anyString());
		verify(userEntityRepository, only()).findByUsername(anyString());
		verify(userEntityRepository, timeout(3000)).findByUsername(anyString());

		verifyNoMoreInteractions(userEntityRepository);
	}
}