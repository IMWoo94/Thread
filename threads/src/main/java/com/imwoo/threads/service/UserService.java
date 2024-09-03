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

}
