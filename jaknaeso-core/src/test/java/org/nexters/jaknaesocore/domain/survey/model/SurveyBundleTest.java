package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.springframework.test.util.ReflectionTestUtils;

class SurveyBundleTest {

  @DisplayName("번들에서 제출하지 않은 설문지를 Survey 리스트로 가져온다.")
  @Test
  void getUnSubmittedSurveyByList() {
    // given
    SurveyBundle surveyBundle = new SurveyBundle();

    BalanceSurvey survey1 = createBalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle, 1L);
    BalanceSurvey survey2 = createBalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle, 2L);
    MultipleChoiceSurvey survey3 = createMultipleChoiceSurvey("나의 행복 지수는", surveyBundle, 3L);
    MultipleChoiceSurvey survey4 = createMultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle, 4L);

    surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));

    // when
    Survey unSubmittedSurvey = surveyBundle.getUnSubmittedSurvey(List.of(survey1, survey3));

    // then
    then(unSubmittedSurvey).isIn(survey2, survey4);
  }

  @DisplayName("모든 설문을 Survey 리스트로 완료한 경우 예외를 던진다.")
  @Test
  void getUnSubmittedSurveyByList_AllSubmitted() {
    // given
    SurveyBundle surveyBundle = new SurveyBundle();

    BalanceSurvey survey1 = createBalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle, 1L);
    BalanceSurvey survey2 = createBalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle, 2L);
    MultipleChoiceSurvey survey3 = createMultipleChoiceSurvey("나의 행복 지수는", surveyBundle, 3L);
    MultipleChoiceSurvey survey4 = createMultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle, 4L);

    surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));

    // when & then
    thenThrownBy(
            () -> surveyBundle.getUnSubmittedSurvey(List.of(survey1, survey2, survey3, survey4)))
        .isEqualTo(CustomException.ALREADY_COMPLETED_SURVEY_BUNDLE);
  }

  @Nested
  @DisplayName("isAllSubmitted 메서드는")
  class isAllSubmitted {

    @Nested
    @DisplayName("모든 설문을 제출한 경우")
    class whenAllSubmitted {

      @DisplayName("true를 반환한다.")
      @Test
      void 모든_설문을_제출했는지_확인한다() {
        // given
        SurveyBundle surveyBundle = new SurveyBundle();

        BalanceSurvey survey1 =
            createBalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle, 1L);
        BalanceSurvey survey2 = createBalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle, 2L);
        MultipleChoiceSurvey survey3 = createMultipleChoiceSurvey("나의 행복 지수는", surveyBundle, 3L);
        MultipleChoiceSurvey survey4 = createMultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle, 4L);

        surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));

        SurveySubmission submission1 = SurveySubmission.builder().survey(survey1).build();

        SurveySubmission submission2 = SurveySubmission.builder().survey(survey2).build();

        SurveySubmission submission3 = SurveySubmission.builder().survey(survey3).build();

        SurveySubmission submission4 = SurveySubmission.builder().survey(survey4).build();

        // when
        boolean result =
            surveyBundle.isAllSubmitted(
                List.of(submission1, submission2, submission3, submission4));
        // then
        then(result).isTrue();
      }
    }

    @DisplayName("모든 설문을 제출하지 않은 경우")
    @Nested
    class whenNotAllSubmitted {

      @DisplayName("false를 반환한다.")
      @Test
      void 모든_설문을_제출했는지_확인한다() {
        // given
        SurveyBundle surveyBundle = new SurveyBundle();

        BalanceSurvey survey1 =
            createBalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle, 1L);
        BalanceSurvey survey2 = createBalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle, 2L);
        MultipleChoiceSurvey survey3 = createMultipleChoiceSurvey("나의 행복 지수는", surveyBundle, 3L);
        MultipleChoiceSurvey survey4 = createMultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle, 4L);

        surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));

        SurveySubmission submission1 = SurveySubmission.builder().survey(survey1).build();
        // when
        boolean result = surveyBundle.isAllSubmitted(List.of(submission1));
        // then
        then(result).isFalse();
      }
    }
  }

  @DisplayName("getNextSurvey 메서드는")
  @Nested
  class getNextSurvey {
    @DisplayName("번들의 모든 설문을 제출한 경우")
    @Nested
    class whenAllSubmitted {
      @DisplayName("예외를 던진다.")
      @Test
      void 모든_설문을_제출했을_때_다음_설문을_가져오려고_할_때() {
        // given
        SurveyBundle surveyBundle = new SurveyBundle();

        BalanceSurvey survey1 =
            createBalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle, 1L);
        BalanceSurvey survey2 = createBalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle, 2L);
        MultipleChoiceSurvey survey3 = createMultipleChoiceSurvey("나의 행복 지수는", surveyBundle, 3L);
        MultipleChoiceSurvey survey4 = createMultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle, 4L);

        surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));

        // when
        // then
        thenThrownBy(() -> surveyBundle.getNextSurvey(List.of(survey1, survey2, survey3, survey4)))
            .isEqualTo(CustomException.ALREADY_COMPLETED_SURVEY_BUNDLE);
      }
    }

    @DisplayName("번들의 모든 설문을 제출하지 않은 경우")
    @Nested
    class whenNotAllSubmitted {
      @Test
      void 번들_내부의_설문_중_제출하지않은_다음_설문을_가져온다() {
        // given
        SurveyBundle surveyBundle = new SurveyBundle();

        BalanceSurvey survey1 =
            createBalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle, 1L);
        BalanceSurvey survey2 = createBalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle, 2L);
        MultipleChoiceSurvey survey3 = createMultipleChoiceSurvey("나의 행복 지수는", surveyBundle, 3L);
        MultipleChoiceSurvey survey4 = createMultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle, 4L);

        surveyBundle.getSurveys().addAll(List.of(survey1, survey2, survey3, survey4));
        // when
        Survey nextSurvey = surveyBundle.getNextSurvey(List.of(survey1, survey2));

        // then
        then(nextSurvey).extracting("id", "content").containsExactly(3L, "나의 행복 지수는");
      }
    }
  }

  private BalanceSurvey createBalanceSurvey(String content, SurveyBundle surveyBundle, Long id) {
    BalanceSurvey survey = new BalanceSurvey(content, surveyBundle);
    ReflectionTestUtils.setField(survey, "id", id);
    return survey;
  }

  private MultipleChoiceSurvey createMultipleChoiceSurvey(
      String content, SurveyBundle surveyBundle, Long id) {
    MultipleChoiceSurvey survey = new MultipleChoiceSurvey(content, surveyBundle);
    ReflectionTestUtils.setField(survey, "id", id);
    return survey;
  }
}
