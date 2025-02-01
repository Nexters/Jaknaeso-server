package org.nexters.jaknaesocore.domain.socialaccount.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.domain.member.model.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "social_account")
@Entity
public class SocialAccount extends BaseTimeEntity {

  private String oauthId;

  @Enumerated(EnumType.STRING)
  private SocialProvider socialProvider;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  private SocialAccount(final String oauthId, final SocialProvider socialProvider) {
    this.oauthId = oauthId;
    this.socialProvider = socialProvider;
  }

  private SocialAccount(
      final String oauthId, final SocialProvider socialProvider, final Member member) {
    this.oauthId = oauthId;
    this.socialProvider = socialProvider;
    this.member = member;
  }

  public static SocialAccount kakaoSignup(final String oauthId) {
    return new SocialAccount(oauthId, SocialProvider.KAKAO);
  }

  public static SocialAccount appleSignUp(final String oauthId, final Member member) {
    return new SocialAccount(oauthId, SocialProvider.APPLE, member);
  }

  public void updateMember(final Member member) {
    this.member = member;
  }
}
