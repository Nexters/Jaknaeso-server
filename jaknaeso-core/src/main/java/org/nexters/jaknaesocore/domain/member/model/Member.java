package org.nexters.jaknaesocore.domain.member.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseTimeEntity {

  @OneToMany(mappedBy = "member")
  private List<SocialAccount> socialAccounts;

  private String name;

  private String email;

  private Member(final String name, final String email) {
    this.name = name;
    this.email = email;
  }

  public static Member create(final String name, final String email) {
    return new Member(name, email);
  }

  public void softDelete() {
    if (this.socialAccounts != null) {
      this.socialAccounts.forEach(SocialAccount::softDelete);
    }
    super.softDelete();
  }

  public void updateUserInfo(final String name, final String email) {
    this.name = name;
    this.email = email;
  }
}
