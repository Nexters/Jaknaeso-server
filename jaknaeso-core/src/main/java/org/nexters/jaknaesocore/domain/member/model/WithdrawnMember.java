package org.nexters.jaknaesocore.domain.member.model;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WithdrawnMember extends BaseEntity {

  private String email;

  private String name;

  private LocalDateTime withdrawnAt;

  private WithdrawnMember(final Member member) {
    this.email = member.getEmail();
    this.name = member.getName();
    this.withdrawnAt = LocalDateTime.now();
  }

  public static WithdrawnMember of(final Member member) {
    return new WithdrawnMember(member);
  }
}
