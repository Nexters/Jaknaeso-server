package org.nexters.jaknaesocore.domain.character.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.CharacterType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class CharacterRecordRepositoryTest extends IntegrationTest {

  @Autowired private CharacterRecordRepository sut;

  @Autowired private CharacterTypeRepository characterTypeRepository;
  @Autowired private CharacterValueReportRepository characterValueReportRepository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @Test
  void 회원의_현재_캐릭터를_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
    final CharacterType characterType1 =
        characterTypeRepository.save(new CharacterType("성취를 쫓는 노력가", "성공 캐릭터 설명"));
    final CharacterType characterType2 =
        characterTypeRepository.save(new CharacterType("성취를 쫓는 노력가", "성공 캐릭터 설명"));
    final CharacterRecord characterRecord1 =
        CharacterRecord.builder()
            .characterNo("첫번째")
            .characterType(characterType1)
            .startDate(LocalDate.now().minusDays(31))
            .endDate(LocalDate.now().minusDays(16))
            .member(member)
            .surveyBundle(bundle)
            .build();
    final CharacterRecord characterRecord2 =
        CharacterRecord.builder()
            .characterNo("두번째")
            .characterType(characterType2)
            .startDate(LocalDate.now().minusDays(15))
            .endDate(LocalDate.now())
            .member(member)
            .surveyBundle(bundle)
            .build();
    sut.saveAll(List.of(characterRecord1, characterRecord2));

    final CharacterRecord actual =
        sut.findTopByMemberIdAndDeletedAtIsNullWithMember(member.getId()).get();

    then(actual).extracting("characterNo", "characterType").containsExactly("두번째", characterType2);
  }

  @Test
  void 회원의_특정_캐릭터를_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
    final CharacterType characterType =
        characterTypeRepository.save(new CharacterType("성취를 쫓는 노력가", "성공 캐릭터 설명"));
    final CharacterRecord characterRecord =
        sut.save(
            CharacterRecord.builder()
                .characterNo("첫번째")
                .characterType(characterType)
                .startDate(LocalDate.now().minusDays(31))
                .endDate(LocalDate.now().minusDays(16))
                .member(member)
                .surveyBundle(bundle)
                .build());

    final CharacterRecord actual =
        sut.findByIdAndMemberIdAndDeletedAtIsNullWithMember(member.getId(), characterRecord.getId())
            .get();

    then(actual).extracting("characterNo", "characterType").containsExactly("첫번째", characterType);
  }

  @Transactional
  @Test
  void 회원의_캐릭터를_설문_번들과_함께_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
    final CharacterType characterType =
        characterTypeRepository.save(new CharacterType("성취를 쫓는 노력가", "성공 캐릭터 설명"));
    sut.save(
        CharacterRecord.builder()
            .characterNo("첫번째")
            .characterType(characterType)
            .startDate(LocalDate.now().minusDays(15))
            .endDate(LocalDate.now())
            .member(member)
            .surveyBundle(bundle)
            .build());

    final List<CharacterRecord> actual =
        sut.findByMemberIdAndDeletedAtIsNullWithMemberAndSurveyBundle(member.getId());

    assertAll(
        () -> then(actual).hasSize(1),
        () -> then(actual.get(0).getSurveyBundle()).isEqualTo(bundle));
  }
}
