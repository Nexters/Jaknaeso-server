package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SELF_DIRECTION;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class SurveysTest {

  @Test
  void 설문_번들에_존재하는_가치관_키워드를_반환한다() {
    final SurveyBundle bundle = new SurveyBundle();
    final BalanceSurvey survey1 =
        new BalanceSurvey(
            "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...", bundle);
    SurveyOption.builder()
        .survey(survey1)
        .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
        .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.ONE)))
        .build();

    final BalanceSurvey survey2 = new BalanceSurvey("회사에서 새로운 평가 시스템을 도입한다. 당신의 선택은?", bundle);
    SurveyOption.builder()
        .survey(survey1)
        .content("업무 성과에 따라 차등 보너스를 지급한다")
        .scores(List.of(KeywordScore.of(SUCCESS, BigDecimal.ONE)))
        .build();

    final Surveys surveys = Surveys.of(List.of(survey1, survey2));

    final Set<Keyword> actual = surveys.getAllKeywords();

    then(actual).hasSize(2).containsExactlyInAnyOrder(SELF_DIRECTION, SUCCESS);
  }

  @Test
  void 설문_번들에_존재하는_특정_가치관_키워드_점수들을_설문별로_그룹화하고_내부적으로_점수를_내림차순으로_정렬하여_반환한다() {
    final SurveyBundle bundle = new SurveyBundle();
    final OnboardingSurvey survey1 =
        new OnboardingSurvey("나는 창의적인 아이디어를 중요하게 생각하고, 나만의 독특한 방식으로 일하는 것을 좋아한다.", bundle);

    SurveyOption.builder()
        .survey(survey1)
        .content("매우 동의해요")
        .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.valueOf(2))))
        .build();
    SurveyOption.builder()
        .survey(survey1)
        .content("조금 동의해요")
        .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.valueOf(1))))
        .build();
    SurveyOption.builder()
        .survey(survey1)
        .content("보통이에요")
        .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.valueOf(0))))
        .build();
    SurveyOption.builder()
        .survey(survey1)
        .content("조금 반대해요")
        .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.valueOf(-1))))
        .build();
    SurveyOption.builder()
        .survey(survey1)
        .content("매우 반대해요")
        .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.valueOf(-2))))
        .build();

    final BalanceSurvey survey2 =
        new BalanceSurvey(
            "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...", bundle);
    SurveyOption.builder()
        .survey(survey2)
        .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
        .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.valueOf(5))))
        .build();

    final Surveys surveys = Surveys.of(List.of(survey1, survey2));

    final Map<Survey, List<KeywordScore>> actual = surveys.getScoresByKeyword(SELF_DIRECTION);

    assertAll(
        () -> then(actual).hasSize(2),
        () ->
            then(actual.values().stream()
                    .map(
                        list ->
                            list.stream().map(KeywordScore::getScore).collect(Collectors.toList())))
                .containsExactlyInAnyOrder(
                    List.of(
                        BigDecimal.valueOf(2),
                        BigDecimal.valueOf(1),
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(-1),
                        BigDecimal.valueOf(-2)),
                    List.of(BigDecimal.valueOf(5))));
  }
}
