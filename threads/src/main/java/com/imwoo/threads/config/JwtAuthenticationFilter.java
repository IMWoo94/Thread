package com.imwoo.threads.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.imwoo.threads.exception.jwt.JwtNotFoundException;
import com.imwoo.threads.service.JwtService;
import com.imwoo.threads.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
/**
 * 요청마다 한번의 filter 적용 되도록 OncePerRequestFilter 사용
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserService userService;
	private final String BEARER_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		log.info("JwtAuthenticationFilter start");

		// TODO JWT 검증
		// Header Authorization 가져오기
		var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		var securityContext = SecurityContextHolder.getContext();

		// Jwt token 존재하지 않는 경우
		if (ObjectUtils.isEmpty(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
			throw new JwtNotFoundException();
		}

		if (!ObjectUtils.isEmpty(authorization)
			&& authorization.startsWith(BEARER_PREFIX)
			&& securityContext.getAuthentication() == null
		) {
			// token 추출
			var accessToken = authorization.substring(BEARER_PREFIX.length());
			var username = jwtService.getUsername(accessToken);
			var userDetails = userService.loadUserByUsername(username);

			var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			securityContext.setAuthentication(authenticationToken);
			SecurityContextHolder.setContext(securityContext);
		}

		filterChain.doFilter(request, response);

	}
}
