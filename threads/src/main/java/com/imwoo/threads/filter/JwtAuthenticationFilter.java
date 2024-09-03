package com.imwoo.threads.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

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
		// Header Authorization 가져오기
		var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		var securityContext = SecurityContextHolder.getContext();

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

		/**
		 * 인증 객체 없는 경우 BasicAuthenticationFilter
		 * ExceptionTranslationFilter 에 의해 처리
		 * 만약 ExceptionTranslationFilter가 AuthenticationException을 감지했다면 authenticationEntryPoint를 실행한다.
		 * 이것을통해서 AbstractSecurityInterceptor의 서브클래스에서 발생하는 인증 실패를 공동으로 처리할 수 있다.
		 *
		 * 만약 ExceptionTranslationFilter가 AccessDeniedException을 감지했다면 해당 사용자가 익명 사용자 인지의 여부를 판별하고 만약 익명 사용자일 경우 동일하게 authenticationEntryPoint을 실행한다.
		 * 만약 익명 사용자가 아닐 경우엔 AccessDeniedHandler을 위임하는데 기본 설정으로 AccessDeniedHandlerImpl을 사용하기때문에 추가 설정이 있지 않는 이상은 그냥 사용하면 된다.
		 */
		filterChain.doFilter(request, response);

	}
}
