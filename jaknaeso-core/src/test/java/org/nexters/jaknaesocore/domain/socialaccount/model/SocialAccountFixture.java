package org.nexters.jaknaesocore.domain.socialaccount.model;

import static org.junit.jupiter.api.Assertions.*;

import lombok.Builder;
import org.nexters.jaknaesocore.domain.member.model.Member;

public class SocialAccountFixture {

  @Builder
  private static SocialAccount createSocialAccount(
      final String oauthId, final SocialProvider socialProvider, final Member member) {
    return new SocialAccount(oauthId, socialProvider, member);
  }
}
