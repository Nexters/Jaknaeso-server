package org.nexters.jaknaesocore.domain.socialaccount.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.RepositoryTest;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialProvider;
import org.springframework.beans.factory.annotation.Autowired;

class SocialAccountRepositoryTest extends RepositoryTest {

  @Autowired SocialAccountRepository socialAccountRepository;

  @DisplayName("oauth_id와 social_provider에 해당하는 소셜 계정을 조회한다.")
  @Test
  void findByOauthIdAndSocialProvider() {
    socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1"));

    Optional<SocialAccount> actual1 =
        socialAccountRepository.findByOauthIdAndSocialProvider("oauthId1", SocialProvider.KAKAO);
    Optional<SocialAccount> actual2 =
        socialAccountRepository.findByOauthIdAndSocialProvider("oauthId2", SocialProvider.KAKAO);

    assertAll(() -> then(actual1).isPresent(), () -> then(actual2).isEmpty());
  }

  @DisplayName("oauth_id와 social_provider에 해당하는 소셜 계정 존재 여부를 조회한다.")
  @Test
  void existsByOauthIdAndSocialProvider() {
    socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1"));

    boolean actual1 =
        socialAccountRepository.existsByOauthIdAndSocialProvider("oauthId1", SocialProvider.KAKAO);
    boolean actual2 =
        socialAccountRepository.existsByOauthIdAndSocialProvider("oauthId2", SocialProvider.KAKAO);

    assertAll(() -> then(actual1).isEqualTo(true), () -> then(actual2).isEqualTo(false));
  }

  @DisplayName("oauth_id에 해당하는 카카오 계정 존재 여부를 조회한다.")
  @Test
  void existsKakaoAccount() {
    socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1"));

    boolean actual1 = socialAccountRepository.existsKakaoAccount("oauthId1");
    boolean actual2 = socialAccountRepository.existsKakaoAccount("oauthId2");

    assertAll(() -> then(actual1).isEqualTo(true), () -> then(actual2).isEqualTo(false));
  }

  @DisplayName("oauth_id에 해당하는 카카오 계정을 조회하고, 존재하는 경우 조회한 계정 정보를 반환하고 존재하지 않으면 새롭게 생성한다.")
  @Test
  void saveKakaoAccount() {
    SocialAccount existedAccount =
        socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1"));

    SocialAccount actual1 = socialAccountRepository.saveKakaoAccount("oauthId1");
    SocialAccount actual2 = socialAccountRepository.saveKakaoAccount("oauthId2");

    assertAll(
        () -> then(actual1).isEqualTo(existedAccount),
        () -> then(actual2.getOauthId()).isEqualTo("oauthId2"));
  }
}
