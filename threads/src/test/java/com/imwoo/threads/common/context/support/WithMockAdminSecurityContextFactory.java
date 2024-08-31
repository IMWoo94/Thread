package com.imwoo.threads.common.context.support;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.imwoo.threads.common.context.support.annotation.WithMockAdmin;

final public class WithMockAdminSecurityContextFactory implements WithSecurityContextFactory<WithMockAdmin> {

	public WithMockAdminSecurityContextFactory() {
	}

	@Override
	public SecurityContext createSecurityContext(WithMockAdmin withMockAdmin) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		User principal = new User(withMockAdmin.username(), withMockAdmin.password(), true, true, true, true,
			List.of(new SimpleGrantedAuthority(withMockAdmin.roles()[0])));
		Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(principal,
			principal.getPassword(), principal.getAuthorities());
		context.setAuthentication(authentication);
		return context;
	}
}
