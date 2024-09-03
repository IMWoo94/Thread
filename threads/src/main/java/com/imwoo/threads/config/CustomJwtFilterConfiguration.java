package com.imwoo.threads.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.imwoo.threads.filter.JwtAuthenticationFilter;

@Configuration
public class CustomJwtFilterConfiguration {

	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> webJwtAuthenticationFilter(
		JwtAuthenticationFilter jwtAuthenticationFilter) {
		FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>(
			jwtAuthenticationFilter);
		registrationBean.setEnabled(false); // 이 설정으로 인해 CustomFilter는 ApplicationFilter에 등록되지 않습니다.
		return registrationBean;
	}
}
