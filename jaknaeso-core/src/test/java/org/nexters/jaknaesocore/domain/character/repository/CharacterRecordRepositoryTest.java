package org.nexters.jaknaesocore.domain.character.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SELF_DIRECTION;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.ValueCharacter;
import org.nexters.jaknaesocore.domain.character.model.ValueReport;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class CharacterRecordRepositoryTest extends IntegrationTest {

  @Autowired private CharacterRecordRepository sut;

  @Autowired private ValueReportRepository valueReportRepository;
  @Autowired private ValueCharacterRepository valueCharacterRepository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;

  @AfterEach
  void tearDown() {
    valueReportRepository.deleteAllInBatch();
    sut.deleteAllInBatch();
    valueCharacterRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @Test
  void 회원의_현재_캐릭터를_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());

    sut.saveAll(
        List.of(
            CharacterRecord.builder()
                .characterNo("첫번째")
                .endDate(LocalDate.now().minusDays(15))
                .member(member)
                .surveyBundle(bundle)
                .build(),
            CharacterRecord.builder()
                .characterNo("두번째")
                .endDate(LocalDate.now())
                .member(member)
                .surveyBundle(bundle)
                .build()));

    final CharacterRecord actual =
        sut.findTopByMemberIdAndDeletedAtIsNullWithMember(member.getId()).get();

    then(actual.getCharacterNo()).isEqualTo("두번째");
  }

  @Test
  void 회원의_특정_캐릭터를_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
    final ValueCharacter valueCharacter =
        valueCharacterRepository.save(new ValueCharacter("성취를 쫓는 노력가", "성공 캐릭터 설명", SUCCESS));
    final CharacterRecord characterRecord =
        sut.save(
            CharacterRecord.builder()
                .characterNo("첫번째")
                .valueCharacter(valueCharacter)
                .member(member)
                .surveyBundle(bundle)
                .build());

    final CharacterRecord actual =
        sut.findByIdAndMemberIdAndDeletedAtIsNullWithMember(member.getId(), characterRecord.getId())
            .get();

    then(actual).extracting("characterNo", "valueCharacter").containsExactly("첫번째", valueCharacter);
  }

  @Transactional
  @Test
  void 회원의_캐릭터를_설문_번들과_함께_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());

    sut.save(CharacterRecord.builder().member(member).surveyBundle(bundle).build());

    final List<CharacterRecord> actual =
        sut.findByMemberIdAndDeletedAtIsNullWithMemberAndSurveyBundle(member.getId());

    assertAll(
        () -> then(actual).hasSize(1),
        () -> then(actual.get(0).getSurveyBundle()).isEqualTo(bundle));
  }

  @Transactional
  @Test
  void 회원의_캐릭터_분석_결과를_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
    final ValueReport valueReport =
        valueReportRepository.save(
            ValueReport.of(SELF_DIRECTION, ScaledBigDecimal.of(BigDecimal.valueOf(100))));

    final CharacterRecord characterRecord =
        sut.save(
            CharacterRecord.builder()
                .characterNo("첫번째")
                .valueReports(List.of(valueReport))
                .member(member)
                .surveyBundle(bundle)
                .build());

    final CharacterRecord actual =
        sut.findByIdAndDeletedAtIsNullWithValueReports(characterRecord.getId()).get();

    then(actual)
        .extracting("valueReports")
        .usingRecursiveComparison()
        .isEqualTo(List.of(valueReport));
  }
}
