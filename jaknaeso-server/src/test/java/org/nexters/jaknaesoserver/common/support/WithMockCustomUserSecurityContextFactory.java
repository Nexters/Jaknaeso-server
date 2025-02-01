package org.nexters.jaknaesoserver.common.support;

import java.util.Collections;
import java.util.List;
import org.nexters.jaknaesoserver.domain.auth.model.CustomUserDetails;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomUser> {
  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    CustomUserDetails principal = new CustomUserDetails(annotation.userId());
    Object credentials = null;
    List<GrantedAuthority> authorities = Collections.emptyList();

    TestingAuthenticationToken authentication =
        new TestingAuthenticationToken(principal, credentials, authorities);
    context.setAuthentication(authentication);

    return context;
  }
}
