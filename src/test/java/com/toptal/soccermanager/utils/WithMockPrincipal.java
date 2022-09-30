package com.toptal.soccermanager.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockPrincipal.WithMockPrincipalSecurityContextFactory.class)
public @interface WithMockPrincipal {
    long id() default 1L;
    String role() default "ROLE_USER";

    class WithMockPrincipalSecurityContextFactory implements WithSecurityContextFactory<WithMockPrincipal> {
        @Override
        public SecurityContext createSecurityContext(WithMockPrincipal principal) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            PrincipalUser principalUser = new PrincipalUser(principal.id(), principal.role());
            Authentication auth = new UsernamePasswordAuthenticationToken(principalUser, null, principalUser.getAuthorities());
            context.setAuthentication(auth);

            return context;
        }
    }
}
