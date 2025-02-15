package org.nexters.jaknaesocore.domain.member.model;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MemberTest {

  @Test
  void 온보딩을_완료한다() {
    // given
    LocalDateTime completedOnboardingAt = LocalDateTime.of(2025, 2, 15, 20, 0);
    Member member = Member.create("나민혁", "nmh9097@gmail.com");
    // when
    member.completeOnboarding(completedOnboardingAt);
    // then
    then(member).extracting("onboardingAt").isEqualTo(LocalDateTime.of(2025, 2, 15, 20, 0));
  }
}
