package com.imwoo.threads.config;

import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.imwoo.threads.filter.JwtAuthenticationFilter;
import com.imwoo.threads.filter.JwtExceptionFilter;
import com.imwoo.threads.service.JwtService;
import com.imwoo.threads.service.UserService;

import lombok.RequiredArgsConstructor;

@TestConfiguration
@RequiredArgsConstructor
public class TestWebSecurityConfiguration {
	public static final String ORIGIN_URL = "https://testCors.com";
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	/**
	 * Jwt CustomFilter 등록에 의해서 MockBean 처리
	 */
	@MockBean
	private JwtService jwtService;
	@MockBean
	private UserService userService;
	private List<String> DEFAULT_EXCLUDE = List.of(
		"/",
		"favicon.ico",
		"/error"
	);
	private List<String> SWAGGER = List.of(
		"/swagger-ui/**",
		"/swagger-ut/**",
		"/v3/api-docs/**"
	);

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(SWAGGER.toArray(new String[0]))
			.requestMatchers(DEFAULT_EXCLUDE.toArray(new String[0]));
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(ORIGIN_URL));
		configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION)); // *
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/api/v1/**", configuration);
		return urlBasedCorsConfigurationSource;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 모든 요청에 대해서 인증 처리를 진행 할 것이다.
		http.authorizeHttpRequests((request) ->
				request
					.requestMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/users/authenticate")
					.permitAll()
					.anyRequest()
					.authenticated()
			)
			.cors(Customizer.withDefaults())
			// REST API 를 개발함으로 Session 관련 생성되지 않도록 처리
			.sessionManagement(
				(session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// csrf 검증은 제외
			.csrf(CsrfConfigurer::disable)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class)
			// 기본 인증 방식을 사용
			.httpBasic(Customizer.withDefaults());

		return http.build();
	}
}
