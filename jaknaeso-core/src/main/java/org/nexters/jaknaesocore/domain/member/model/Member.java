package org.nexters.jaknaesocore.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.support.model.BaseEntity;
import org.nexters.jaknaesocore.domain.auth.model.SocialProvider;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @Column(name = "oauth_id")
  private String oauthId;

  @Enumerated(EnumType.STRING)
  @Column(name = "social_provider")
  private SocialProvider socialProvider;

  private Member(final String oauthId, final SocialProvider socialProvider) {
    this.oauthId = oauthId;
    this.socialProvider = socialProvider;
  }

  public static Member kakaoSignup(final String oauthId) {
    return new Member(oauthId, SocialProvider.KAKAO);
  }
}
