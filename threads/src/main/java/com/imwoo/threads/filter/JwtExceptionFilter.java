package com.imwoo.threads.filter;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (JwtException e) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setCharacterEncoding("UTF-8");

			var errorMap = new HashMap<String, Object>();
			errorMap.put("status", HttpStatus.UNAUTHORIZED);
			errorMap.put("error", e.getMessage());

			ObjectMapper objectMapper = new ObjectMapper();
			String responseJson = objectMapper.writeValueAsString(errorMap);

			response.getWriter().write(responseJson);
		}

	}
}
