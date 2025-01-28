package org.nexters.jaknaesocore.domain.member.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.domain.auth.model.SocialProvider;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);

  boolean existsByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);

  default boolean existsKakaoMember(final String oauthId) {
    return existsByOauthIdAndSocialProvider(oauthId, SocialProvider.KAKAO);
  }

  default Member saveKakaoMember(final String oauthId) {
    return findByOauthIdAndSocialProvider(oauthId, SocialProvider.KAKAO)
        .orElseGet(() -> save(Member.kakaoSignup(oauthId)));
  }
}
