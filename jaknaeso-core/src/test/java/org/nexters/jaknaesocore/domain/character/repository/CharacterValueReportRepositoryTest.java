package org.nexters.jaknaesocore.domain.character.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.BENEVOLENCE;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SELF_DIRECTION;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.STABILITY;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.CharacterType;
import org.nexters.jaknaesocore.domain.character.model.CharacterValueReport;
import org.nexters.jaknaesocore.domain.character.model.ValueReports;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.BalanceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetricsMap;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.KeywordWeightMap;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class CharacterValueReportRepositoryTest extends IntegrationTest {

  @Autowired private CharacterValueReportRepository sut;

  @Autowired private CharacterTypeRepository characterTypeRepository;
  @Autowired private CharacterRecordRepository characterRecordRepository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @AfterEach
  void tearDown() {
    sut.deleteAllInBatch();
    characterRecordRepository.deleteAllInBatch();
    characterTypeRepository.deleteAllInBatch();
    surveySubmissionRepository.deleteAllInBatch();
    surveyOptionRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @Transactional
  @Test
  void 회원의_캐릭터_분석_결과를_조회한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
    final BalanceSurvey survey1 =
        new BalanceSurvey(
            "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...", bundle);
    final SurveyOption option1 =
        SurveyOption.builder()
            .survey(survey1)
            .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
            .scores(
                List.of(
                    KeywordScore.builder().keyword(SELF_DIRECTION).score(BigDecimal.ONE).build()))
            .build();

    final BalanceSurvey survey2 = new BalanceSurvey("독립에 대한 고민이 깊어지는 요즘... 드디어 결정을 내렸다.", bundle);
    final SurveyOption option2 =
        SurveyOption.builder()
            .survey(survey2)
            .content("내 취향대로 꾸민 집에서 자유롭게 생활하기")
            .scores(
                List.of(
                    KeywordScore.builder().keyword(SELF_DIRECTION).score(BigDecimal.ONE).build()))
            .build();

    final BalanceSurvey survey3 =
        new BalanceSurvey("바쁜 일상에 지쳐버린 나. 여가 시간을 더 의미 있게 보내고 싶어졌다.", bundle);
    final SurveyOption option3 =
        SurveyOption.builder()
            .survey(survey3)
            .content("매년 새로운 취미에 도전하며 색다른 즐거움 찾기")
            .scores(
                List.of(KeywordScore.builder().keyword(STABILITY).score(BigDecimal.ONE).build()))
            .build();

    final BalanceSurvey survey4 = new BalanceSurvey("회사에서 새로운 평가 시스템을 도입한다. 당신의 선택은?", bundle);
    final SurveyOption option4 =
        SurveyOption.builder()
            .survey(survey4)
            .content("업무 성과에 따라 차등 보너스를 지급한다")
            .scores(List.of(KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.ONE).build()))
            .build();

    final BalanceSurvey survey5 =
        new BalanceSurvey("연애를 시작한지도 어연 3개월, 그 사람과 나의 연애는 꽤 잘 맞는다. 우리의 관계는...", bundle);
    final SurveyOption option5 =
        SurveyOption.builder()
            .survey(survey5)
            .content("서로의 일상 속에서 따뜻하게 지지하는 관계")
            .scores(
                List.of(KeywordScore.builder().keyword(BENEVOLENCE).score(BigDecimal.ONE).build()))
            .build();

    final List<SurveySubmission> submissions =
        List.of(
            SurveySubmission.builder().survey(survey1).selectedOption(option1).build(),
            SurveySubmission.builder().survey(survey2).selectedOption(option2).build(),
            SurveySubmission.builder().survey(survey3).selectedOption(option3).build(),
            SurveySubmission.builder().survey(survey4).selectedOption(option4).build(),
            SurveySubmission.builder().survey(survey5).selectedOption(option5).build());

    final Map<Keyword, BigDecimal> weights = new HashMap<>();
    weights.put(SELF_DIRECTION, BigDecimal.valueOf(5));
    weights.put(STABILITY, BigDecimal.valueOf(25));
    weights.put(SUCCESS, BigDecimal.valueOf(25));
    weights.put(BENEVOLENCE, BigDecimal.valueOf(5));

    final List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(SELF_DIRECTION).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(STABILITY).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(BENEVOLENCE).score(BigDecimal.valueOf(1)).build());

    final Map<Keyword, KeywordMetrics> metricsMap = KeywordMetricsMap.generate(scores);
    final Map<Keyword, BigDecimal> weightMap = KeywordWeightMap.generate(metricsMap);

    final CharacterType characterType =
        characterTypeRepository.save(new CharacterType("성취를 쫓는 노력가", "성공 캐릭터 설명", Keyword.SUCCESS));
    final CharacterRecord characterRecord =
        characterRecordRepository.save(
            CharacterRecord.builder()
                .characterNo("첫번째")
                .characterType(characterType)
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now())
                .member(member)
                .surveyBundle(bundle)
                .build());
    final CharacterValueReport report =
        new CharacterValueReport(
            characterRecord, ValueReports.of(weightMap, metricsMap, submissions).getReports());

    final CharacterValueReport actual =
        sut.findByCharacterIdAndDeletedAtIsNullWithCharacterAndValueReports(characterRecord.getId())
            .get();

    then(actual)
        .extracting("valueReports")
        .usingRecursiveComparison()
        .isEqualTo(ValueReports.of(weightMap, metricsMap, submissions).getReports());
  }
}
