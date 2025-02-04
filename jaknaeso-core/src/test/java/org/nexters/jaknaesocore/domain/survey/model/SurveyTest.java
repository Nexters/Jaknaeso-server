package org.nexters.jaknaesocore.domain.survey.model;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.springframework.test.util.ReflectionTestUtils;

class SurveyTest {

  @Test
  void 설문의_선택지를_ID를_통해_찾는다() {
    // given
    SurveyBundle surveyBundle = new SurveyBundle();
    BalanceSurvey survey = new BalanceSurvey("설문", surveyBundle);

    SurveyOption option1 = createSurveyOptionWithId(survey, "옵션1", 1L);
    SurveyOption option2 = createSurveyOptionWithId(survey, "옵션2", 2L);
    SurveyOption option3 = createSurveyOptionWithId(survey, "옵션3", 3L);

    // when
    SurveyOption foundOption = survey.getOptionById(2L);

    // then
    BDDAssertions.then(foundOption).extracting("id", "content").containsExactly(2L, "옵션2");
  }

  @Test
  void 설문의_선택지를_ID를_통해_찾을_수_없을_때_예외를_발생한다() {
    // given
    SurveyBundle surveyBundle = new SurveyBundle();
    BalanceSurvey survey = new BalanceSurvey("설문", surveyBundle);

    SurveyOption option1 = createSurveyOptionWithId(survey, "옵션1", 1L);
    SurveyOption option2 = createSurveyOptionWithId(survey, "옵션2", 2L);
    SurveyOption option3 = createSurveyOptionWithId(survey, "옵션3", 3L);
    // when
    // then
    BDDAssertions.thenThrownBy(() -> survey.getOptionById(4L))
        .isInstanceOf(CustomException.class)
        .isEqualTo(CustomException.SURVEY_OPTION_NOT_FOUND);
  }

  private SurveyOption createSurveyOptionWithId(Survey survey, String content, Long id) {
    SurveyOption option = SurveyOption.builder().survey(survey).content(content).build();
    ReflectionTestUtils.setField(option, "id", id);
    return option;
  }
}
