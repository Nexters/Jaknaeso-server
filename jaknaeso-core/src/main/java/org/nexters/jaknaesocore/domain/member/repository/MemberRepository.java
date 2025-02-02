package org.nexters.jaknaesocore.domain.member.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByIdAndDeletedAtIsNull(final Long id);

  default Member findMember(final Long memberId) {
    return findByIdAndDeletedAtIsNull(memberId)
        .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));
  }

  default void softDeleteById(final Long memberId) {
    Member member = findMember(memberId);
    member.softDelete();
  }
}
