package com.imwoo.threads.config;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			log.info("JwtExceptionFilter start");
			filterChain.doFilter(request, response);

		} catch (JwtException e) {
			log.info("JwtExceptionFilter start {}", e.getMessage());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setCharacterEncoding("UTF-8");

			var errorMap = new HashMap<String, Object>();
			errorMap.put("status", HttpStatus.UNAUTHORIZED);
			errorMap.put("error", e.getMessage());

			ObjectMapper objectMapper = new ObjectMapper();
			String responseJson = objectMapper.writeValueAsString(errorMap);

			response.getWriter().write(responseJson);
		} catch (Exception e) {
			log.info("JwtExceptionFilter exception 짜자잔 {}", e.getMessage());
		}

	}
}
