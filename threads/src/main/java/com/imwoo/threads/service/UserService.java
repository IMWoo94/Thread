package com.imwoo.threads.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.imwoo.threads.exception.user.UserDuplicatedException;
import com.imwoo.threads.exception.user.UserNotAllowedException;
import com.imwoo.threads.exception.user.UserNotFoundException;
import com.imwoo.threads.model.entity.UserEntity;
import com.imwoo.threads.model.user.User;
import com.imwoo.threads.model.user.request.UserUpdateRequest;
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

	public List<User> getUsers(String query) {
		List<UserEntity> userEntities;

		if (query != null && !query.isBlank()) {
			// TODO : query 검색어 기반, 해당 검색하기 username 에 포함되는 유저목록 가져오기.
			// 쿼리 Like 예약어로 간단한 검색은 가능하지만 차후 elasticsearch 기반 엔진을 사용해서 좀더 검색에 용이한 방식으로 구현해보기
			userEntities = userEntityRepository.findByUsernameContaining(query);
		} else {
			// 전체 유저 검색
			userEntities = userEntityRepository.findAll();
		}

		return userEntities
			.stream()
			.map(User::from)
			.toList();
	}

	public User getUser(String username) {
		var userEntity = userEntityRepository.findByUsername(username)
			.orElseThrow(() -> new UserNotFoundException(username));

		return User.from(userEntity);
	}

	public User updateUser(String username, UserUpdateRequest userUpdateRequest, UserEntity currentUserEntity) {
		var userEntity = userEntityRepository.findByUsername(username)
			.orElseThrow(() -> new UserNotFoundException(username));

		// TODO : 동일 사용자만 수정 권한 부여
		if (!currentUserEntity.equals(userEntity)) {
			throw new UserNotAllowedException();
		}

		if (userUpdateRequest.description() != null) {
			// TODO 트랜잭션 사용해서 JPA 영속성 컨텍스트의 더티 체킹 활용 해보기. / 현재는 Merge 방식으로 적용
			userEntity.setDescription(userUpdateRequest.description());
			userEntityRepository.save(userEntity);
		}

		return User.from(userEntity);
	}
}
