package org.nexters.jaknaesocore.domain.survey.model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Surveys {

  private final Map<Survey, List<KeywordScore>> scores;

  /**
   * @param surveys 특정 설문 번들에 들어있는 모든 설문 목록
   */
  public static Surveys of(List<Survey> surveys) {
    return new Surveys(
        surveys.stream()
            .collect(
                Collectors.toMap(
                    survey -> survey,
                    survey ->
                        survey.getOptions().stream()
                            .flatMap(option -> option.getScores().stream())
                            .collect(Collectors.toList()))));
  }

  /**
   * @return 설문에 포함된 모든 가치관 키워드
   */
  public Set<Keyword> getAllKeywords() {
    return scores.values().stream()
        .flatMap(List::stream)
        .map(KeywordScore::getKeyword)
        .collect(Collectors.toSet());
  }

  /**
   * 주어진 가치관 키워드와 일치하는 점수 설문별로 조회
   *
   * @param keyword 가치관 키워드
   * @return 특정 가치관 키워드에 대한 설문별 점수 목록
   */
  public Map<Survey, List<KeywordScore>> getScoresByKeyword(final Keyword keyword) {
    return scores.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry ->
                    entry.getValue().stream()
                        .filter(keywordScore -> keywordScore.getKeyword().equals(keyword))
                        .sorted(Comparator.comparing(KeywordScore::getScore).reversed())
                        .collect(Collectors.toList())))
        .entrySet()
        .stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
