package org.nexters.jaknaesocore.domain.character.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.BENEVOLENCE;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SELF_DIRECTION;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.STABILITY;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
import org.nexters.jaknaesocore.domain.character.repository.ValueReportRepository;
import org.nexters.jaknaesocore.domain.survey.model.BalanceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.springframework.beans.factory.annotation.Autowired;

class ValueReportsTest extends IntegrationTest {

  @Autowired private CharacterRecordRepository characterRecordRepository;
  @Autowired private ValueReportRepository valueReportRepository;

  private Map<Keyword, KeywordMetrics> generateMetricsMap(List<KeywordScore> scores) {
    return scores.stream()
        .collect(Collectors.groupingBy(KeywordScore::getKeyword, Collectors.toList()))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, entry -> KeywordMetrics.create(entry.getValue())));
  }

  private Map<Keyword, BigDecimal> generateWeightsMap(
      final Map<Keyword, KeywordMetrics> metricsMap) {
    Map<Keyword, BigDecimal> weightMap = new HashMap<>();

    int keywordCnt = metricsMap.size();
    ScaledBigDecimal sumPerKeyword =
        ScaledBigDecimal.of(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(keywordCnt));

    metricsMap.forEach(
        (k, v) -> {
          var sum = v.getPositive().subtract(v.getNegative());
          weightMap.put(k, sumPerKeyword.divide(sum).getValue());
        });
    return weightMap;
  }

  @Test
  void 키워드_가중치와_설문_응답_목록으로_가치관_리포트를_반환한다() {
    final List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(SELF_DIRECTION).score(BigDecimal.valueOf(2)).build(),
            KeywordScore.builder().keyword(STABILITY).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(BENEVOLENCE).score(BigDecimal.valueOf(1)).build());

    final Map<Keyword, KeywordMetrics> metricsMap = generateMetricsMap(scores);
    final Map<Keyword, BigDecimal> weightMap = generateWeightsMap(metricsMap);

    final SurveyBundle bundle = new SurveyBundle();
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

    final List<ValueReport> actual = ValueReports.report(weightMap, metricsMap, submissions);

    assertAll(
        () ->
            then(actual)
                .contains(
                    ValueReport.of(SELF_DIRECTION, ScaledBigDecimal.of(BigDecimal.valueOf(100)))),
        () ->
            then(actual)
                .contains(ValueReport.of(STABILITY, ScaledBigDecimal.of(BigDecimal.valueOf(100)))),
        () ->
            then(actual)
                .contains(ValueReport.of(SUCCESS, ScaledBigDecimal.of(BigDecimal.valueOf(100)))),
        () ->
            then(actual)
                .contains(
                    ValueReport.of(BENEVOLENCE, ScaledBigDecimal.of(BigDecimal.valueOf(100)))));
  }
}
