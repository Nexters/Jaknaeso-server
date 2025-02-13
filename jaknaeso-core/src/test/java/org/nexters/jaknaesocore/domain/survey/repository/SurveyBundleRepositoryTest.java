package org.nexters.jaknaesocore.domain.survey.repository;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.springframework.beans.factory.annotation.Autowired;

class SurveyBundleRepositoryTest extends IntegrationTest {

  @Autowired private SurveyBundleRepository surveyBundleRepository;

  @AfterEach
  void tearDown() {
    surveyBundleRepository.deleteAllInBatch();
  }

  @Test
  void 주어진_ID_목록에_없는_번들_중_가장_작은_ID를_가진_번들을_찾는다() {
    // given
    SurveyBundle surveyBundle1 = new SurveyBundle();
    SurveyBundle surveyBundle2 = new SurveyBundle();
    SurveyBundle surveyBundle3 = new SurveyBundle();
    SurveyBundle surveyBundle4 = new SurveyBundle();

    surveyBundleRepository.saveAll(
        List.of(surveyBundle1, surveyBundle2, surveyBundle3, surveyBundle4));

    List<Long> excludedIds = List.of(surveyBundle1.getId(), surveyBundle2.getId());

    // when
    Optional<SurveyBundle> result =
        surveyBundleRepository.findFirstByIdNotInOrderByIdAsc(excludedIds);

    // then
    then(result)
        .hasValueSatisfying(
            surveyBundle -> then(surveyBundle.getId()).isEqualTo(surveyBundle3.getId()));
  }

  @Test
  void 모든_번들이_제외_목록에_있으면_빈_값을_반환한다() {
    // given
    SurveyBundle surveyBundle1 = new SurveyBundle();
    SurveyBundle surveyBundle2 = new SurveyBundle();
    SurveyBundle surveyBundle3 = new SurveyBundle();
    SurveyBundle surveyBundle4 = new SurveyBundle();

    surveyBundleRepository.saveAll(
        List.of(surveyBundle1, surveyBundle2, surveyBundle3, surveyBundle4));

    List<Long> excludedIds =
        List.of(
            surveyBundle1.getId(),
            surveyBundle2.getId(),
            surveyBundle3.getId(),
            surveyBundle4.getId());

    // when
    Optional<SurveyBundle> result =
        surveyBundleRepository.findFirstByIdNotInOrderByIdAsc(excludedIds);

    // then
    then(result).isEmpty();
  }
}
