package org.nexters.jaknaesocore.domain.character.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.springframework.beans.factory.annotation.Autowired;

class CharacterRecordsTest extends IntegrationTest {

  @Autowired private SurveyBundleRepository surveyBundleRepository;

  @Test
  void 번들_아이디로_캐릭터가_완성되었는지_확인한다() {
    final SurveyBundle completeBundle1 = surveyBundleRepository.save(new SurveyBundle());
    final SurveyBundle completeBundle2 = surveyBundleRepository.save(new SurveyBundle());
    final SurveyBundle incompleteBundle = surveyBundleRepository.save(new SurveyBundle());
    final CharacterRecords characterRecords =
        new CharacterRecords(
            List.of(
                CharacterRecord.builder().characterNo("첫번째").surveyBundle(completeBundle1).build(),
                CharacterRecord.builder()
                    .characterNo("두번째")
                    .surveyBundle(completeBundle2)
                    .build()));

    final boolean actual1 = characterRecords.isIncompleteCharacter(completeBundle1.getId());
    final boolean actual2 = characterRecords.isIncompleteCharacter(completeBundle2.getId());
    final boolean actual3 = characterRecords.isIncompleteCharacter(incompleteBundle.getId());

    assertAll(
        () -> then(actual1).isEqualTo(false),
        () -> then(actual2).isEqualTo(false),
        () -> then(actual3).isEqualTo(true));
  }
}
