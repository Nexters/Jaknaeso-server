package org.nexters.jaknaesocore.domain.member.repository;

import org.nexters.jaknaesocore.domain.auth.model.SocialProvider;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByOauthIdAndSocialProvider(
      final String oauthId, final SocialProvider socialProvider);

  default boolean existsKakaoMember(final String oauthId) {
    return existsByOauthIdAndSocialProvider(oauthId, SocialProvider.KAKAO);
  }
}
