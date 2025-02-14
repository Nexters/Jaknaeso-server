package org.nexters.jaknaesocore.domain.member.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.RepositoryTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryTest extends RepositoryTest {

  private @Autowired MemberRepository memberRepository;

  @Test
  void 아이디에_해당하는_멤버를_조회한다() {
    Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));

    then(memberRepository.findMember(member.getId())).isEqualTo(member);
  }

  @Test
  void 아이디에_해당하는_멤버가_없는_경우_MEBMER_NOT_FOUND_예외를_반환하고_조회에_실패한다() {
    thenThrownBy(() -> memberRepository.findMember(1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorType.MEMBER_NOT_FOUND.getMessage());
  }
}
