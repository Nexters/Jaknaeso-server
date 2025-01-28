package org.nexters.jaknaesocore.domain.socialaccount.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

  Optional<SocialAccount> findByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);

  boolean existsByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);

  default boolean existsKakaoAccount(final String oauthId) {
    return existsByOauthIdAndSocialProvider(oauthId, SocialProvider.KAKAO);
  }

  default SocialAccount saveKakaoAccount(final String oauthId) {
    return findByOauthIdAndSocialProvider(oauthId, SocialProvider.KAKAO)
        .orElseGet(() -> save(SocialAccount.kakaoSignup(oauthId)));
  }
}
