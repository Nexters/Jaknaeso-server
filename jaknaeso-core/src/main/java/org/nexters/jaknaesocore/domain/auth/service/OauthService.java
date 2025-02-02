package org.nexters.jaknaesocore.domain.auth.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nexters.jaknaesocore.common.support.MediaTypeValueBuilder;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.auth.model.AppleAuthorization;
import org.nexters.jaknaesocore.domain.auth.model.AppleIdToken;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoAuthClient;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenCommand;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

  private static final String BEARER_PREFIX = "Bearer ";
  private static String FORM_URLENCODED_MEDIA_TYPE =
      MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE).charset("utf-8").build();

  private final SocialAccountRepository socialAccountRepository;
  private final MemberRepository memberRepository;
  private final KakaoClient kakaoClient;
  private final KakaoAuthClient kakaoAuthClient;
  private final ObjectMapper objectMapper;

  @Value("${oauth.kakao.client-id}")
  private String clientId;

  @Value("${oauth.kakao.client-secret}")
  private String clientSecret;

  @Value("${oauth.kakao.redirect-uri}")
  private String redirectUri;

  @Transactional
  public Long kakaoLogin(final KakaoLoginCommand command) {
    KakaoTokenResponse token = getKakaoToken(command.authorizationCode());
    log.info("kakao 토큰 받아오기 성공");
    KakaoUserInfoResponse userInfo = getKakaoUserInfo(token.accessToken());
    log.info("kakao 사용자 정보 받아오기 성공");

    String oauthId = userInfo.id().toString();
    SocialAccount socialAccount = socialAccountRepository.saveKakaoAccount(oauthId);

    Member member = socialAccount.getMember();
    if (member == null) {
      member = memberRepository.save(Member.create());
      socialAccount.updateMember(member);
    }
    return member.getId();
  }

  private KakaoUserInfoResponse getKakaoUserInfo(final String accessToken) {
    return kakaoClient.requestUserInfo(BEARER_PREFIX + accessToken);
  }

  private KakaoTokenResponse getKakaoToken(final String authorizationCode) {
    KakaoTokenCommand command =
        KakaoTokenCommand.of(clientId, clientSecret, redirectUri, authorizationCode);
    return kakaoAuthClient.requestToken(command);
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
