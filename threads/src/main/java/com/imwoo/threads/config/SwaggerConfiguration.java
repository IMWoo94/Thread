package com.imwoo.threads.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfiguration {

	/**
	 * JWT 방식의 인증으로 Header Bearer Token 설정
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		var apiKey = new SecurityScheme()
			.type(SecurityScheme.Type.APIKEY)
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		var securityRequirement = new SecurityRequirement().addList("Json Web Token Bearer");

		return new OpenAPI()
			.components(new Components().addSecuritySchemes("Json Web Token Bearer", apiKey))
			.addSecurityItem(securityRequirement);
	}
}
