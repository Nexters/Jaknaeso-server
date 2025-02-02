package org.nexters.jaknaesocore.domain.auth.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.ServiceTest;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenResponse;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

class OauthServiceTest extends ServiceTest {

  static final String BEARER_PREFIX = "Bearer ";

  @Autowired OauthService oauthService;

  @DisplayName("처음 카카오 계정으로 로그인하면 소셜 계정과 멤버를 생성하고 생성한 멤버 아이디를 반환한다.")
  @Test
  void initialKakaoLoginSuccess() {
    Long oauthId = 1L;
    Member newMember = Member.create();
    ReflectionTestUtils.setField(newMember, "id", 1L);
    SocialAccount newAccount = SocialAccount.kakaoSignup(oauthId.toString());

    KakaoLoginCommand command = new KakaoLoginCommand("authorization code");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willReturn(new KakaoTokenResponse("bearer", "access token", 1, "refresh token", 1));
    given(kakaoClient.requestUserInfo(BEARER_PREFIX + "access token"))
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

    KakaoLoginCommand command = new KakaoLoginCommand("authorization code");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willReturn(new KakaoTokenResponse("bearer", "access token", 1, "refresh token", 1));
    given(kakaoClient.requestUserInfo(BEARER_PREFIX + "access token"))
        .willReturn(new KakaoUserInfoResponse(oauthId));
    given(socialAccountRepository.saveKakaoAccount(oauthId.toString())).willReturn(account);

    then(oauthService.kakaoLogin(command)).isEqualTo(1L);
  }

  @DisplayName("카카오 토큰 API 호출에 실패하면 RestClientException를 반환한다.")
  @Test
  void kakaoLoginFailByTokenApiFailure() {
    KakaoLoginCommand command = new KakaoLoginCommand("authorization code");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willThrow(RestClientException.class);

    thenThrownBy(() -> oauthService.kakaoLogin(command)).isInstanceOf(RestClientException.class);
  }

  @DisplayName("카카오 사용자 정보 API 호출에 실패하면 RestClientException를 반환한다.")
  @Test
  void kakaoLoginFailByUserInfoApiFailure() {
    KakaoLoginCommand command = new KakaoLoginCommand("authorization code");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willReturn(new KakaoTokenResponse("bearer", "access token", 1, "refresh token", 1));
    given(kakaoClient.requestUserInfo(BEARER_PREFIX + "access token"))
        .willThrow(RestClientException.class);

    thenThrownBy(() -> oauthService.kakaoLogin(command)).isInstanceOf(RestClientException.class);
  }

  private MultiValueMap<String, String> makeKakaoTokenRequestParams(
      final String authorizationCode) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", "client-id");
    params.add("client_secret", "client-secret");
    params.add("code", authorizationCode);
    params.add("redirect_uri", "redirect-uri");
    return params;
  }
}
