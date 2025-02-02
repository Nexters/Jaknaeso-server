package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;

@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "survey_type")
@Entity
public abstract class Survey extends BaseTimeEntity {
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bundle_id")
  private SurveyBundle surveyBundle;

  @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SurveyOption> options = new ArrayList<>();

  public Survey(final String content, final SurveyBundle surveyBundle) {
    this.content = content;
    this.surveyBundle = surveyBundle;
    surveyBundle.addSurvey(this);
  }

  public void addOption(final SurveyOption option) {
    options.add(option);
  }

  public abstract SurveyType getSurveyType();
}
