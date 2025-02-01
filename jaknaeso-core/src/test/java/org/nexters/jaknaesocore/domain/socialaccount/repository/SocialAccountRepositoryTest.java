package org.nexters.jaknaesocore.domain.socialaccount.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.RepositoryTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialProvider;
import org.springframework.beans.factory.annotation.Autowired;

class SocialAccountRepositoryTest extends RepositoryTest {

  @Autowired SocialAccountRepository socialAccountRepository;
  @Autowired MemberRepository memberRepository;

  @DisplayName("oauth_id와 social_provider에 해당하는 소셜 계정을 조회한다.")
  @Test
  void findByOauthIdAndSocialProvider() {
    Member member = memberRepository.save(Member.create());
    socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1", member));

    Optional<SocialAccount> actual1 =
        socialAccountRepository.findByOauthIdAndSocialProvider("oauthId1", SocialProvider.KAKAO);
    Optional<SocialAccount> actual2 =
        socialAccountRepository.findByOauthIdAndSocialProvider("oauthId2", SocialProvider.KAKAO);

    assertAll(() -> then(actual1).isPresent(), () -> then(actual2).isEmpty());
  }

  @DisplayName("oauth_id와 social_provider에 해당하는 소셜 계정 존재 여부를 조회한다.")
  @Test
  void existsByOauthIdAndSocialProvider() {
    Member member = memberRepository.save(Member.create());
    socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1", member));

    boolean actual1 =
        socialAccountRepository.existsByOauthIdAndSocialProvider("oauthId1", SocialProvider.KAKAO);
    boolean actual2 =
        socialAccountRepository.existsByOauthIdAndSocialProvider("oauthId2", SocialProvider.KAKAO);

    assertAll(() -> then(actual1).isEqualTo(true), () -> then(actual2).isEqualTo(false));
  }
}
