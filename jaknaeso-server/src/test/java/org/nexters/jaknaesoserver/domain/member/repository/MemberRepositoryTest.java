package org.nexters.jaknaesoserver.domain.member.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.config.JpaConfig;
import org.nexters.jaknaesocore.domain.auth.model.SocialProvider;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({JpaConfig.class})
@DataJpaTest
class MemberRepositoryTest {

  @Autowired MemberRepository memberRepository;

  @DisplayName("oauth_id와 social_provider에 해당하는 멤버 존재 여부를 조회한다.")
  @Test
  void existsByOauthIdAndSocialProvider() {
    memberRepository.save(Member.kakaoSignup("oauthId1"));

    boolean actual1 =
        memberRepository.existsByOauthIdAndSocialProvider("oauthId1", SocialProvider.KAKAO);
    boolean actual2 =
        memberRepository.existsByOauthIdAndSocialProvider("oauthId2", SocialProvider.KAKAO);

    assertAll(() -> then(actual1).isEqualTo(true), () -> then(actual2).isEqualTo(false));
  }

  @DisplayName("oauth_id에 해당하는 카카오 멤버 존재 여부를 조회한다.")
  @Test
  void existsKakaoMember() {
    memberRepository.save(Member.kakaoSignup("oauthId1"));

    boolean actual1 = memberRepository.existsKakaoMember("oauthId1");
    boolean actual2 = memberRepository.existsKakaoMember("oauthId2");

    assertAll(() -> then(actual1).isEqualTo(true), () -> then(actual2).isEqualTo(false));
  }
}
