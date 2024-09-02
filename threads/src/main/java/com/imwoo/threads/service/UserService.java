package com.imwoo.threads.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.imwoo.threads.exception.user.UserNotFoundException;
import com.imwoo.threads.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/**
 * InMemoryUserDetailsManager 를 대체할 UserDetailsService
 */
public class UserService implements UserDetailsService {

	private final UserEntityRepository userEntityRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userEntityRepository.findByUsername(username)
			.orElseThrow(() -> new UserNotFoundException(username));
	}
}
