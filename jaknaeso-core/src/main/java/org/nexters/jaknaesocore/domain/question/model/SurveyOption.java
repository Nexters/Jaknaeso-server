package org.nexters.jaknaesocore.domain.question.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SurveyOption extends BaseTimeEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "survey_id")
  private Survey survey;

  private String content;

  @ElementCollection
  @CollectionTable(name = "keyword_scores", joinColumns = @JoinColumn(name = "survey_option_id"))
  private List<KeywordScore> scores = new ArrayList<>();

  @Builder
  private SurveyOption(Survey survey, String content, List<KeywordScore> scores) {
    this.survey = survey;
    this.content = content;
    this.scores = scores;
    survey.addOption(this);
  }
}
