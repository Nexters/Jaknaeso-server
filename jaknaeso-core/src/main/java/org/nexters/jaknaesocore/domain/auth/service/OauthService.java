package org.nexters.jaknaesocore.domain.auth.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.MediaTypeValueBuilder;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoAuthClient;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenCommand;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenResponse;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.repository.SocialAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Value("${oauth.kakao.client-id}")
  private String clientId;

  @Value("${oauth.kakao.client-secret}")
  private String clientSecret;

  @Value("${oauth.kakao.redirect-uri}")
  private String redirectUri;

  @Transactional
  public Long kakaoLogin(final KakaoLoginCommand command) {
    KakaoTokenResponse token = getKakaoToken(command.authorizationCode());
    KakaoUserInfoResponse userInfo = getKakaoUserInfo(token.accessToken());

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
    return kakaoClient.requestUserInfo(BEARER_PREFIX + accessToken, FORM_URLENCODED_MEDIA_TYPE);
  }

  private KakaoTokenResponse getKakaoToken(final String authorizationCode) {
    KakaoTokenCommand command =
        KakaoTokenCommand.of(clientId, clientSecret, redirectUri, authorizationCode);
    return kakaoAuthClient.requestToken(FORM_URLENCODED_MEDIA_TYPE, command);
  }
}
