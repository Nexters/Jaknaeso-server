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
    final KakaoTokenResponse token = getKakaoToken(command.authorizationCode(), command.redirectUri());
    log.info("kakao 토큰 받아오기 완료");
    final KakaoUserInfoResponse userInfo = getKakaoUserInfo(token.getAccessToken());
    log.info("kakao 사용자 정보 받아오기 완료");

    final String oauthId = userInfo.getId().toString();
    final Member member = findKakaoMember(oauthId);
    if (member == null) {
      return signupKakaoMember(command.authorizationCode()).getId();
    }
    return member.getId();
  }

  private Member signupKakaoMember(final String oauthId) {
    final Member member = memberRepository.save(Member.create());
    socialAccountRepository.save(SocialAccount.kakaoSignup(oauthId, member));
    return member;
  }

  private Member findKakaoMember(final String oauthId) {
    return socialAccountRepository
        .findByOauthIdAndSocialProvider(oauthId, SocialProvider.KAKAO)
        .map(SocialAccount::getMember)
        .filter(member -> member.getDeletedAt() == null)
        .orElse(null);
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

  @Transactional
  public Long appleLogin(final AppleLoginCommand command) {
    AppleIdToken appleIdToken = AppleIdToken.of(command.idToken());
    final String jwtClaims = appleIdToken.getJwtClaims();
    final AppleAuthorization authorization = decodeAppleIdTokenPayload(jwtClaims);

    return socialAccountRepository
        .findByOauthIdAndSocialProvider(authorization.getSub(), SocialProvider.APPLE)
        .map(account -> account.getMember().getId())
        .orElseGet(
            () -> {
              final Member member = memberRepository.save(Member.create());
              socialAccountRepository.save(
                  SocialAccount.appleSignUp(authorization.getSub(), member));
              return member.getId();
            });
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
}
