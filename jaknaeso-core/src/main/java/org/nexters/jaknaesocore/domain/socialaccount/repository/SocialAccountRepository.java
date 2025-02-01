package org.nexters.jaknaesocore.domain.socialaccount.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

  @Query(
      "SELECT sa FROM SocialAccount sa LEFT JOIN FETCH sa.member "
          + "WHERE sa.oauthId = :oauthId AND sa.socialProvider = :socialProvider")
  Optional<SocialAccount> findByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);

  boolean existsByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);
}
