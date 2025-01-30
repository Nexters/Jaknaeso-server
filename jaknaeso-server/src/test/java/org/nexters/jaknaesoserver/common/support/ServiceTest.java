package org.nexters.jaknaesoserver.common.support;

import org.nexters.jaknaesocore.common.http.httpinterface.HttpInterfaceFactoryBeanPostProcessor;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.repository.SocialAccountRepository;
import org.nexters.jaknaesoserver.config.SecurityConfig;
import org.nexters.jaknaesoserver.domain.auth.service.JwtService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class ServiceTest extends IntegrationTest {

  @MockitoBean protected SocialAccountRepository socialAccountRepository;

  @MockitoBean protected MemberRepository memberRepository;

  @MockitoBean protected KakaoClient kakaoClient;

  @MockitoBean protected SecurityConfig securityConfig;

  @MockitoBean protected JwtService jwtService;

  @MockitoBean
  protected HttpInterfaceFactoryBeanPostProcessor httpInterfaceFactoryBeanPostProcessor;
}
