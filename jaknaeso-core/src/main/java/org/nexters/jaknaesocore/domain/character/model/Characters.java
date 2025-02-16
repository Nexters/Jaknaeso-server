package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Characters {

  private final Long characterNo;
  private final Member member;
  private final SurveyBundle bundle;
  private final List<KeywordScore> scores;
  private final List<SurveySubmission> submissions;
  private final Map<Keyword, ValueCharacter> valueCharacters;

  @Builder
  public static Characters of(
      final Long characterNo,
      final Member member,
      final SurveyBundle bundle,
      final List<KeywordScore> scores,
      final List<SurveySubmission> submissions,
      final Map<Keyword, ValueCharacter> valueCharacters) {
    return new Characters(characterNo, member, bundle, scores, submissions, valueCharacters);
  }

  public CharacterRecord provideCharacterRecord() {
    final List<ValueReport> valueReports = provideValueReport();
    final ValueCharacter valueCharacter = findTopValueCharacter(valueReports);
    return CharacterRecord.builder()
        .characterNo("TODO 수정")
        .valueCharacter(valueCharacter)
        .valueReports(valueReports)
        .member(member)
        .surveyBundle(bundle)
        .valueReports(valueReports)
        .startDate(bundle.getCreatedAt().toLocalDate())
        .endDate(LocalDate.now())
        .build();
  }

  private List<ValueReport> provideValueReport() {
    final Map<Keyword, KeywordMetrics> metricsMap = calculateKeywordMetrics(scores);
    final Map<Keyword, BigDecimal> weightMap = calculateKeywordWeights(metricsMap);

    return ValueReports.report(weightMap, metricsMap, submissions);
  }

  private Map<Keyword, KeywordMetrics> calculateKeywordMetrics(final List<KeywordScore> scores) {
    return scores.stream()
        .collect(Collectors.groupingBy(KeywordScore::getKeyword, Collectors.toList()))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, entry -> KeywordMetrics.create(entry.getValue())));
  }

  private Map<Keyword, BigDecimal> calculateKeywordWeights(
      final Map<Keyword, KeywordMetrics> metricsMap) {
    int keywordCnt = metricsMap.size();
    ScaledBigDecimal sumPerKeyword =
        ScaledBigDecimal.of(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(keywordCnt));

    Map<Keyword, BigDecimal> weightMap = new HashMap<>();
    metricsMap.forEach(
        (k, v) -> {
          var sum = v.getPositive().subtract(v.getNegative());
          weightMap.put(k, sumPerKeyword.divide(sum).getValue());
        });
    return weightMap;
  }

  private ValueCharacter findTopValueCharacter(final List<ValueReport> valueReports) {
    final ValueReport topReport =
        valueReports.stream()
            .max(Comparator.comparing(ValueReport::getPercentage))
            .orElseThrow(() -> new IllegalStateException("ValueReport가 비어 있습니다."));

    return valueCharacters.get(topReport.getKeyword());
  }
}
