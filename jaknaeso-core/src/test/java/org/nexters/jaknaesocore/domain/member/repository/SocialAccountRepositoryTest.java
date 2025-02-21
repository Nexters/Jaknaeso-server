package org.nexters.jaknaesocore.domain.member.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.RepositoryTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.model.SocialAccount;
import org.nexters.jaknaesocore.domain.member.model.SocialProvider;
import org.springframework.beans.factory.annotation.Autowired;

class SocialAccountRepositoryTest extends RepositoryTest {

  @Autowired SocialAccountRepository socialAccountRepository;
  @Autowired MemberRepository memberRepository;

  @Test
  void oauthId와_socialProvider에_해당하는_소셜_계정을_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1", member));

    final Optional<SocialAccount> actual1 =
        socialAccountRepository.findByOauthIdAndSocialProviderAndDeletedAtIsNull(
            "oauthId1", SocialProvider.KAKAO);
    final Optional<SocialAccount> actual2 =
        socialAccountRepository.findByOauthIdAndSocialProviderAndDeletedAtIsNull(
            "oauthId2", SocialProvider.KAKAO);

    assertAll(() -> then(actual1).isPresent(), () -> then(actual2).isEmpty());
  }

  @Test
  void oauthId와_socialProvider에_해당하는_소셜_계정_존재_여부를_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1", member));

    final boolean actual1 =
        socialAccountRepository.existsByOauthIdAndSocialProvider("oauthId1", SocialProvider.KAKAO);
    final boolean actual2 =
        socialAccountRepository.existsByOauthIdAndSocialProvider("oauthId2", SocialProvider.KAKAO);

    assertAll(() -> then(actual1).isEqualTo(true), () -> then(actual2).isEqualTo(false));
  }
}
