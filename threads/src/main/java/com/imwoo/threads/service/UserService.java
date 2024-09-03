package com.imwoo.threads.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.imwoo.threads.exception.user.UserDuplicatedException;
import com.imwoo.threads.exception.user.UserNotFoundException;
import com.imwoo.threads.model.entity.UserEntity;
import com.imwoo.threads.model.user.User;
import com.imwoo.threads.model.user.response.UserAuthenticationResponse;
import com.imwoo.threads.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/**
 * InMemoryUserDetailsManager 를 대체할 UserDetailsService
 */
public class UserService implements UserDetailsService {

	private final UserEntityRepository userEntityRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userEntityRepository.findByUsername(username)
			.orElseThrow(() -> new UserNotFoundException(username));
	}

	public User signUp(String username, String password) {
		userEntityRepository.findByUsername(username)
			.ifPresent(
				user -> {
					throw new UserDuplicatedException(username);
				}
			);

		var userEntity = UserEntity.of(username, passwordEncoder.encode(password));
		userEntityRepository.save(userEntity);

		return User.from(userEntity);
	}

	public UserAuthenticationResponse authenticate(String username, String password) {
		var userEntity = userEntityRepository.findByUsername(username)
			.orElseThrow(() -> new UserNotFoundException(username));

		// 패스워드 일치 확인
		if (passwordEncoder.matches(password, userEntity.getPassword())) {
			var accessToken = jwtService.generateAccessToken(userEntity);
			return new UserAuthenticationResponse(accessToken);
		} else {
			// 패스워드 불일치 예외 발생
			// TODO : 패스워드 불일치 시 고려할 사항을 차후 적용해보기. 일단은 단순 일치 정보자 없다고 제공
			throw new UserNotFoundException(username);
		}
	}
}
