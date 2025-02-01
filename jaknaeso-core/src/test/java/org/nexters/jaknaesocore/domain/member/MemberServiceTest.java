package org.nexters.jaknaesocore.domain.member;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.ServiceTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends ServiceTest {

  @Autowired MemberService memberService;

  @DisplayName("멤버를 삭제한다.")
  @Test
  void deleteMemberSuccess() {
    willDoNothing().given(memberRepository).deleteMember(1L);

    memberService.deleteMember(1L);

    then(memberRepository).should().deleteMember(1L);
  }

  @DisplayName("멤버 삭제에 실패하면 CustomException을 반환한다.")
  @Test
  void deleteMemberFail() {
    willThrow(CustomException.class).given(memberRepository).deleteMember(1L);

    thenThrownBy(() -> memberService.deleteMember(1L)).isInstanceOf(CustomException.class);
  }
}
