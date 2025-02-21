package org.nexters.jaknaesocore.domain.member.model;

import lombok.Builder;

public class SocialAccountFixture {

  @Builder
  private static SocialAccount createSocialAccount(
      final String oauthId, final SocialProvider socialProvider, final Member member) {
    return new SocialAccount(oauthId, socialProvider, member);
  }
}
