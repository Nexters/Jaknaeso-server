package org.nexters.jaknaesocore.domain.question.model;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SurveyBundleTest {

  @DisplayName("번들에서 제출하지 않은 설문지를 가져온다.")
  @Test
  void getUnSubmittedSurvey() {
    // given
    SurveyBundle surveyBundle = new SurveyBundle();

    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);
    ReflectionTestUtils.setField(survey1, "id", 1L);
    ReflectionTestUtils.setField(survey2, "id", 2L);
    ReflectionTestUtils.setField(survey3, "id", 3L);
    ReflectionTestUtils.setField(survey4, "id", 4L);

    surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));

    // when
    Survey unSubmittedSurvey = surveyBundle.getUnSubmittedSurvey(Set.of(1L, 3L));
    // then
    then(unSubmittedSurvey).extracting("id").isNotEqualTo(1L);
    then(unSubmittedSurvey).extracting("id").isNotEqualTo(3L);
  }

  @DisplayName("모든 설문을 완료한 경우 예외를 던진다.")
  @Test
  void getUnSubmittedSurvey_AllSubmitted() {
    // given
    SurveyBundle surveyBundle = new SurveyBundle();

    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);
    ReflectionTestUtils.setField(survey1, "id", 1L);
    ReflectionTestUtils.setField(survey2, "id", 2L);
    ReflectionTestUtils.setField(survey3, "id", 3L);
    ReflectionTestUtils.setField(survey4, "id", 4L);

    surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));

    thenThrownBy(() -> surveyBundle.getUnSubmittedSurvey(Set.of(1L, 2L, 3L, 4L)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("모든 설문을 완료하셨습니다.");
  }
}
