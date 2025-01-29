package org.nexters.jaknaesoserver.common.support;

import org.nexters.jaknaesoserver.config.SecurityConfig;
import org.nexters.jaknaesoserver.domain.auth.service.JwtParser;
import org.nexters.jaknaesoserver.domain.auth.service.JwtProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class RepositoryTest extends IntegrationTest {

  @MockitoBean protected SecurityConfig securityConfig;

  @MockitoBean protected JwtParser jwtParser;

  @MockitoBean protected JwtProvider jwtProvider;
}
