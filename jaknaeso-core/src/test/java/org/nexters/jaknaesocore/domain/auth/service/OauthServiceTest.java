package org.nexters.jaknaesocore.domain.auth.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.nexters.jaknaesocore.domain.socialaccount.model.SocialProvider.KAKAO;

import java.util.Optional;
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

  @Test
  void 처음_카카오_계정으로_로그인하면_소셜_계정과_멤버를_생성하고_생성한_멤버_아이디를_반환한다() {
    Long oauthId = 1L;
    Member newMember = createMemberWithId(1L);
    SocialAccount newAccount = SocialAccount.kakaoSignup(oauthId.toString(), newMember);

    KakaoLoginCommand command = new KakaoLoginCommand("authorization code", "redirect-uri");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willReturn(new KakaoTokenResponse("bearer", "access token", 1, "refresh token", 1));
    given(kakaoClient.requestUserInfo(BEARER_PREFIX + "access token"))
        .willReturn(new KakaoUserInfoResponse(oauthId));
    given(
            socialAccountRepository.findByOauthIdAndSocialProviderAndDeletedAtIsNull(
                oauthId.toString(), KAKAO))
        .willReturn(Optional.empty());
    given(memberRepository.save(any(Member.class))).willReturn(newMember);
    given(socialAccountRepository.save(newAccount)).willReturn(newAccount);

    then(oauthService.kakaoLogin(command)).isEqualTo(1L);
  }

  @Test
  void 카카오_계정으로_로그인하면_소셜_계정으로_멤버를_조회하고_조회한_멤버_아이디를_반환한다() {
    Long oauthId = 1L;
    Member member = createMemberWithId(1L);
    SocialAccount account = createSocialAccountWithOauthIdAndMember(oauthId.toString(), member);

    KakaoLoginCommand command = new KakaoLoginCommand("authorization code", "redirect-uri");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willReturn(new KakaoTokenResponse("bearer", "access token", 1, "refresh token", 1));
    given(kakaoClient.requestUserInfo(BEARER_PREFIX + "access token"))
        .willReturn(new KakaoUserInfoResponse(oauthId));
    given(
            socialAccountRepository.findByOauthIdAndSocialProviderAndDeletedAtIsNull(
                oauthId.toString(), KAKAO))
        .willReturn(Optional.of(account));

    then(oauthService.kakaoLogin(command)).isEqualTo(1L);
  }

  @Test
  void 카카오_토큰_API_호출에_실패하면_RestClientException를_반환한다() {
    KakaoLoginCommand command = new KakaoLoginCommand("authorization code", "redirect-uri");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willThrow(RestClientException.class);

    thenThrownBy(() -> oauthService.kakaoLogin(command)).isInstanceOf(RestClientException.class);
  }

  @Test
  void 카카오_사용자_정보_API_호출에_실패하면_RestClientException를_반환한다() {
    KakaoLoginCommand command = new KakaoLoginCommand("authorization code", "redirect-uri");

    given(kakaoAuthClient.requestToken(makeKakaoTokenRequestParams(command.authorizationCode())))
        .willReturn(new KakaoTokenResponse("bearer", "access token", 1, "refresh token", 1));
    given(kakaoClient.requestUserInfo(BEARER_PREFIX + "access token"))
        .willThrow(RestClientException.class);

    thenThrownBy(() -> oauthService.kakaoLogin(command)).isInstanceOf(RestClientException.class);
  }

  private Member createMemberWithId(final Long id) {
    Member member = Member.create();
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }

  private SocialAccount createSocialAccountWithOauthIdAndMember(
      final String oauthId, final Member member) {
    SocialAccount account = SocialAccount.kakaoSignup(oauthId, member);
    ReflectionTestUtils.setField(account, "member", member);
    return account;
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
