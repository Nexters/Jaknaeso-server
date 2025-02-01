package org.nexters.jaknaesocore.domain.member.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.RepositoryTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryTest extends RepositoryTest {

  @Autowired MemberRepository memberRepository;

  @DisplayName("아이디에 해당하는 멤버를 조회한다.")
  @Test
  void findMemberSuccess() {
    Member member = memberRepository.save(Member.create());

    then(memberRepository.findMember(member.getId())).isEqualTo(member);
  }

  @DisplayName("아이디에 해당하는 멤버가 없는 경우, MEBMER_NOT_FOUND 예외를 반환하고 조회에 실패한다.")
  @Test
  void findMemberFail() {
    thenThrownBy(() -> memberRepository.findMember(1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorType.MEMBER_NOT_FOUND.getMessage());
  }
}
