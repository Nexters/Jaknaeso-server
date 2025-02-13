package org.nexters.jaknaesocore.domain.member.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

  @Query(
      "SELECT m FROM Member m LEFT JOIN FETCH m.socialAccounts "
          + "WHERE m.id = :id AND m.deletedAt IS NULL")
  Optional<Member> findByIdAndDeletedAtIsNull(final Long id);

  default Member findMember(final Long memberId) {
    return findByIdAndDeletedAtIsNull(memberId)
        .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));
  }
}
