package com.imwoo.threads.common.context.support;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.imwoo.threads.common.context.support.annotation.WithMockAdmin;
import com.imwoo.threads.model.entity.UserEntity;

final public class WithMockAdminSecurityContextFactory implements WithSecurityContextFactory<WithMockAdmin> {

	public WithMockAdminSecurityContextFactory() {
	}

	@Override
	public SecurityContext createSecurityContext(WithMockAdmin withMockAdmin) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		// User principal = new User(withMockAdmin.username(), withMockAdmin.password(), true, true, true, true,
		// 	List.of(new SimpleGrantedAuthority(withMockAdmin.roles()[0])));
		// Custom UserDetails ( UserEntity ) return
		UserDetails userDetails = new UserEntity(1L, withMockAdmin.username(), withMockAdmin.password(), null, null,
			ZonedDateTime.now(), ZonedDateTime.now(), null);
		Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails,
			null, List.of(new SimpleGrantedAuthority(withMockAdmin.roles()[0])));
		context.setAuthentication(authentication);
		return context;
	}
}
