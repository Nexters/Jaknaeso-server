package org.nexters.jaknaesocore.domain.member.model;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.mock.TestLocalDateTimeHolder;

class MemberTest {

  @Test
  void 온보딩을_완료한다() {
    // given
    LocalDateTime completedOnboardingAt = LocalDateTime.of(2025, 2, 15, 20, 0);
    Member member = Member.create("나민혁", "nmh9097@gmail.com");
    // when
    member.completeOnboarding(new TestLocalDateTimeHolder(completedOnboardingAt));
    // then
    then(member)
        .extracting("completedOnboardingAt")
        .isEqualTo(LocalDateTime.of(2025, 2, 15, 20, 0));
  }

  @Test
  void 온보딩을_완료했는지_확인한다() {
    // given
    LocalDateTime completedOnboardingAt = LocalDateTime.of(2025, 2, 15, 20, 0);
    Member member = Member.create("나민혁", "nmh9097@gmail.com");
    // when
    boolean sut = member.isCompletedOnboarding();
    // then
    then(sut).isFalse();
  }
}
