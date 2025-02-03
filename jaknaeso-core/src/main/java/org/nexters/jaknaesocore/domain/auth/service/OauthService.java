package org.nexters.jaknaesocore.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.auth.model.AppleAuthorization;
import org.nexters.jaknaesocore.domain.auth.model.AppleIdToken;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoAuthClient;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenResponse;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.nexters.jaknaesocore.domain.auth.service.dto.AppleLoginCommand;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginWithTokenCommand;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialProvider;
import org.nexters.jaknaesocore.domain.socialaccount.repository.SocialAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

  private static final String BEARER_PREFIX = "Bearer ";

  private final SocialAccountRepository socialAccountRepository;
  private final MemberRepository memberRepository;
  private final KakaoClient kakaoClient;
  private final KakaoAuthClient kakaoAuthClient;
  private final ObjectMapper objectMapper;

  @Value("${oauth.kakao.client-id}")
  private String clientId;

  @Value("${oauth.kakao.client-secret}")
  private String clientSecret;

  @Transactional
  public Long kakaoLogin(final KakaoLoginCommand command) {
    final KakaoTokenResponse token =
        getKakaoToken(command.authorizationCode(), command.redirectUri());
    final KakaoUserInfoResponse userInfo = getKakaoUserInfo(token.accessToken());

    final String oauthId = userInfo.id().toString();
    return socialAccountRepository
        .findByOauthIdAndSocialProviderAndDeletedAtIsNull(oauthId, SocialProvider.KAKAO)
        .map(SocialAccount::getMember)
        .map(
            it -> {
              it.updateUserInfo(userInfo.kakaoAccount().name(), userInfo.kakaoAccount().email());
              return it.getId();
            })
        .orElseGet(
            () ->
                kakaoSignUp(
                    oauthId, userInfo.kakaoAccount().name(), userInfo.kakaoAccount().email()));
  }

  @Transactional
  public Long kakaoLoginWithToken(final KakaoLoginWithTokenCommand command) {
    final KakaoUserInfoResponse userInfo = getKakaoUserInfo(command.accessToken());

    final String oauthId = userInfo.id().toString();
    final Member member = findMemberWithSocialProvider(oauthId, SocialProvider.KAKAO);
    if (member == null) {
      final Member newMember =
          memberRepository.save(
              Member.create(userInfo.kakaoAccount().name(), userInfo.kakaoAccount().email()));
      socialAccountRepository.save(SocialAccount.kakaoSignup(oauthId, newMember));
      return newMember.getId();
    }
    return member.getId();
  }

  private KakaoUserInfoResponse getKakaoUserInfo(final String accessToken) {
    return kakaoClient.requestUserInfo(BEARER_PREFIX + accessToken);
  }

  private KakaoTokenResponse getKakaoToken(
      final String authorizationCode, final String redirectUri) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("code", authorizationCode);
    params.add("redirect_uri", redirectUri);
    return kakaoAuthClient.requestToken(params);
  }

  private Long kakaoSignUp(final String oauthId, final String name, final String email) {
    final Member member = memberRepository.save(Member.create(name, email));
    socialAccountRepository.save(SocialAccount.kakaoSignup(oauthId, member));
    return member.getId();
  }

  @Transactional
  public Long appleLogin(final AppleLoginCommand command) {
    AppleIdToken appleIdToken = AppleIdToken.of(command.idToken());
    final String jwtClaims = appleIdToken.decodePayload();
    final AppleAuthorization authorization = decodeAppleIdTokenPayload(jwtClaims);

    return socialAccountRepository
        .findByOauthIdAndSocialProviderAndDeletedAtIsNull(
            authorization.getSub(), SocialProvider.APPLE)
        .map(account -> account.getMember().getId())
        .orElseGet(
            () -> appleSignup(authorization.getSub(), command.name(), authorization.getEmail()));
  }

  private AppleAuthorization decodeAppleIdTokenPayload(final String appleJwtClaims) {
    try {
      return objectMapper.readValue(appleJwtClaims, AppleAuthorization.class);
    } catch (JsonProcessingException e) {
      log.error(
          "decodeJwtToken: {}-{} / jwtToken: {}", e.getMessage(), e.getCause(), appleJwtClaims);
      throw CustomException.INVALID_APPLE_ID_TOKEN;
    }
  }

  private Long appleSignup(final String oauthId, final String name, final String email) {
    final Member member = memberRepository.save(Member.create(name, email));
    socialAccountRepository.save(SocialAccount.appleSignUp(oauthId, member));
    return member.getId();
  }
}
