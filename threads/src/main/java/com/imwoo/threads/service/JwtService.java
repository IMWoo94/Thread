package com.imwoo.threads.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

	// TODO properties 등 외부적으로 노출되지 않은 key 로 사용 및 멀티 key 사용 고민
	private static final SecretKey key = Jwts.SIG.HS256.key().build();

	public String generateAccessToken(UserDetails userDetails) {
		return generateToken(userDetails.getUsername());
	}

	public String getUsername(String accessToken) {
		return getSubject(accessToken);
	}

	private String generateToken(String subject) {
		var now = new Date();
		// 현재 시점 3시간 이후 만료
		var exp = new Date(now.getTime() + (1000 * 60 * 60 * 3));
		return Jwts.builder()
			.subject(subject)
			.signWith(key)
			.issuedAt(now)
			.expiration(exp)
			.compact();
	}

	private String getSubject(String token) {
		try {
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
		} catch (JwtException e) {
			// TODO Jwt 에러 응답 추후 생성
			log.error("JwtException : {}", e.getMessage());
			throw e;
		}
	}
}
