package com.imwoo.threads.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebSecurityConfiguration {

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(List.of("Authorization")); // *
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/api/v1/**", configuration);
		return urlBasedCorsConfigurationSource;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 모든 요청에 대해서 인증 처리를 진행 할 것이다.
		http.authorizeHttpRequests((request) -> request.anyRequest().authenticated())
			.cors(Customizer.withDefaults())
			// REST API 를 개발함으로 Session 관련 생성되지 않도록 처리
			.sessionManagement(
				(session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// csrf 검증은 제외
			.csrf(CsrfConfigurer::disable)
			// 기본 인증 방식을 사용
			.httpBasic(Customizer.withDefaults());

		return http.build();
	}
}
