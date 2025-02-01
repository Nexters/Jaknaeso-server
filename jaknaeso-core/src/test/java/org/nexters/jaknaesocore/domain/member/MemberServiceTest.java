package org.nexters.jaknaesocore.domain.member;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.ServiceTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends ServiceTest {

  @Autowired MemberService memberService;

  @Test
  void 멤버를_삭제한다() {
    given(memberRepository.findMember(1L)).willReturn(Member.create());

    memberService.deleteMember(1L);

    then(memberRepository).should().deleteById(1L);
  }

  @Test
  void 멤버_삭제에_실패하면_CustomException을_반환한다() {
    willThrow(CustomException.class).given(memberRepository).findMember(1L);

    thenThrownBy(() -> memberService.deleteMember(1L)).isInstanceOf(CustomException.class);
  }
}
