package org.nexters.jaknaesocore.domain.character.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.survey.model.Surveys;

public class ScoreEvaluator {

  private final Surveys surveys;
  private final List<SurveySubmission> submissions;

  @Builder
  private ScoreEvaluator(final Surveys surveys, final List<SurveySubmission> submissions) {
    this.surveys = surveys;
    this.submissions = submissions;
  }

  public static ScoreEvaluator of(final Surveys surveys, final List<SurveySubmission> submissions) {
    return new ScoreEvaluator(surveys, submissions);
  }

  public List<ValueReport> generateValueReports() {
    return provideValueReport();
  }

  private List<ValueReport> provideValueReport() {
    final Map<Keyword, KeywordMetrics> metricsMap = calculateKeywordMetrics(surveys);
    return ValueReports.report(metricsMap, submissions);
  }

  private Map<Keyword, KeywordMetrics> calculateKeywordMetrics(final Surveys surveys) {
    return surveys.getAllKeywords().stream()
        .collect(
            Collectors.toMap(
                keyword -> keyword,
                keyword -> {
                  Map<Long, List<KeywordScore>> surveyScores =
                      surveys.getScoresByKeyword(keyword).entrySet().stream()
                          .collect(
                              Collectors.toMap(
                                  entry -> entry.getKey().getId(), Map.Entry::getValue));
                  return KeywordMetrics.of(surveyScores);
                }));
  }
}
