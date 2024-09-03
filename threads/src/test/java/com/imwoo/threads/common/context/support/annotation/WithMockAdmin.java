package com.imwoo.threads.common.context.support.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

import com.imwoo.threads.common.context.support.WithMockAdminSecurityContextFactory;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(
	factory = WithMockAdminSecurityContextFactory.class
)
public @interface WithMockAdmin {
	String value() default "admin";

	String username() default "admin";

	String[] roles() default {"USER"};

	String[] authorities() default {};

	String password() default "admin";

	@AliasFor(
		annotation = WithSecurityContext.class
	)
	TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;
}
