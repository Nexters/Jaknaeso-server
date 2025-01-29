package org.nexters.jaknaesocore.domain.auth.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.http.httpinterface.HttpInterfaceFactoryBeanPostProcessor;
import org.nexters.jaknaesocore.common.http.support.MediaTypeValueBuilder;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.repository.SocialAccountRepository;
import org.nexters.jaknaesoserver.common.support.IntegrationTest;
import org.nexters.jaknaesoserver.config.SecurityConfig;
import org.nexters.jaknaesoserver.domain.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

class OauthServiceTest extends IntegrationTest {

  static final String BEARER_PREFIX = "Bearer ";
  static final String MEDIA_TYPE =
      MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE).charset("utf-8").build();

  @Autowired OauthService oauthService;

  @MockitoBean SocialAccountRepository socialAccountRepository;

  @MockitoBean MemberRepository memberRepository;

  @MockitoBean KakaoClient kakaoClient;

  @MockitoBean SecurityConfig securityConfig;

  @MockitoBean JwtService jwtService;

  @MockitoBean HttpInterfaceFactoryBeanPostProcessor httpInterfaceFactoryBeanPostProcessor;

  @DisplayName("처음 카카오 계정으로 로그인하면 소셜 계정과 멤버를 생성하고 생성한 멤버 아이디를 반환한다.")
  @Test
  void initialKakaoLoginSuccess() {
    Long oauthId = 1L;
    Member newMember = Member.create();
    ReflectionTestUtils.setField(newMember, "id", 1L);
    SocialAccount newAccount = SocialAccount.kakaoSignup(oauthId.toString());
    KakaoLoginCommand command = new KakaoLoginCommand("access token");

    given(kakaoClient.requestUserInfo(BEARER_PREFIX + command.accessToken(), MEDIA_TYPE))
        .willReturn(new KakaoUserInfoResponse(oauthId));
    given(socialAccountRepository.saveKakaoAccount(oauthId.toString())).willReturn(newAccount);
    given(memberRepository.save(any(Member.class))).willReturn(newMember);

    then(oauthService.kakaoLogin(command)).isEqualTo(1L);
  }

  @DisplayName("카카오 계정으로 로그인하면 소셜 계정으로 멤버를 조회하고 조회한 멤버 아이디를 반환한다.")
  @Test
  void kakaoLoginSuccess() {
    Long oauthId = 1L;
    Member member = Member.create();
    ReflectionTestUtils.setField(member, "id", 1L);
    SocialAccount account = SocialAccount.kakaoSignup(oauthId.toString());
    ReflectionTestUtils.setField(account, "member", member);
    KakaoLoginCommand command = new KakaoLoginCommand("access token");

    given(kakaoClient.requestUserInfo(BEARER_PREFIX + command.accessToken(), MEDIA_TYPE))
        .willReturn(new KakaoUserInfoResponse(oauthId));
    given(socialAccountRepository.saveKakaoAccount(oauthId.toString())).willReturn(account);

    then(oauthService.kakaoLogin(command)).isEqualTo(1L);
  }

  @DisplayName("카카오 로그인 API 호출에 실패하면 RestClientException를 반환한다.")
  @Test
  void kakaoLoginFail() {
    Long oauthId = 1L;
    KakaoLoginCommand command = new KakaoLoginCommand("access token");

    given(kakaoClient.requestUserInfo(BEARER_PREFIX + command.accessToken(), MEDIA_TYPE))
        .willThrow(RestClientException.class);

    thenThrownBy(() -> oauthService.kakaoLogin(command)).isInstanceOf(RestClientException.class);
  }
}
