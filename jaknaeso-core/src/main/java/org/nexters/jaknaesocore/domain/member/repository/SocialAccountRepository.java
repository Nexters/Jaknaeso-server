package org.nexters.jaknaesocore.domain.member.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.SocialAccount;
import org.nexters.jaknaesocore.domain.member.model.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

  Optional<SocialAccount> findByIdAndDeletedAtIsNull(Long id);

  @Query(
      "SELECT sa FROM SocialAccount sa LEFT JOIN FETCH sa.member "
          + "WHERE sa.oauthId = :oauthId AND sa.socialProvider = :socialProvider AND sa.deletedAt IS NULL")
  Optional<SocialAccount> findByOauthIdAndSocialProviderAndDeletedAtIsNull(
      final String oauthId, final SocialProvider socialProvider);

  boolean existsByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);

  default SocialAccount findSocialAccount(final Long id) {
    return findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new CustomException(ErrorType.SOCIAL_ACCOUNT_NOT_FOUND));
  }
}
