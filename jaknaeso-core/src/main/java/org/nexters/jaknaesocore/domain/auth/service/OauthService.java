package org.nexters.jaknaesocore.domain.auth.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.MediaTypeValueBuilder;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.repository.SocialAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OauthService {

  private static final String BEARER_PREFIX = "Bearer ";

  private final SocialAccountRepository socialAccountRepository;
  private final MemberRepository memberRepository;
  private final KakaoClient kakaoClient;

  @Transactional
  public Long kakaoLogin(final KakaoLoginCommand command) {
    KakaoUserInfoResponse userInfo = getKakaoUserInfo(command.accessToken());

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
    String mediaType =
        MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE).charset("utf-8").build();

    return kakaoClient.requestUserInfo(BEARER_PREFIX + accessToken, mediaType);
  }
}
