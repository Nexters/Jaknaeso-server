package org.nexters.jaknaesocore.domain.auth.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.http.MediaTypeValueBuilder;
import org.nexters.jaknaesocore.domain.auth.dto.LoginKakaoCommand;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

  private final MemberRepository memberRepository;

  private final KakaoClient kakaoClient;
//  private final JwtProvider jwtProvider;

  private final String BEARER_TOKEN_FORMAT = "Bearer %s";

  @Transactional
  public void loginKakao(final LoginKakaoCommand command) {
    String mediaType = MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE)
        .charset("utf-8")
        .build();
    KakaoUserInfoResponse userInfo = kakaoClient.requestUserInfo(
        String.format(BEARER_TOKEN_FORMAT, command.accessToken()), mediaType
    );

    String oauthId = userInfo.id().toString();
    if (!memberRepository.existsKakaoMember(oauthId)) {
      memberRepository.save(Member.kakaoSignup(oauthId));
    }
    // TODO: JWT 발급 로직
  }
}
