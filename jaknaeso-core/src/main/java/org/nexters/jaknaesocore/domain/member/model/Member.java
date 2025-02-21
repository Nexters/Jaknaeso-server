package org.nexters.jaknaesocore.domain.member.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

  private static final String ANONYMIZE_PREFIX = "withdrawn-";

  @OneToMany(mappedBy = "member")
  private List<SocialAccount> socialAccounts;

  private String name;

  private String email;

  private LocalDateTime completedOnboardingAt;

  private Member(final String name, final String email) {
    this.name = name;
    this.email = email;
  }

  public static Member create(final String name, final String email) {
    return new Member(name, email);
  }

  @Override
  public void softDelete() {
    if (this.socialAccounts != null) {
      this.socialAccounts.forEach(SocialAccount::softDelete);
    }
    this.anonymize();
    super.softDelete();
  }

  private void anonymize() {
    final String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
    this.name = ANONYMIZE_PREFIX + uniqueSuffix;
    this.email = ANONYMIZE_PREFIX + uniqueSuffix;
  }

  public void updateUserInfo(final String name, final String email) {
    this.name = name;
    this.email = email;
  }

  public void completeOnboarding(LocalDateTime onboardingAt) {
    this.completedOnboardingAt = onboardingAt;
  }

  public boolean isCompletedOnboarding() {
    return this.completedOnboardingAt != null;
  }
}
