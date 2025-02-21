package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;

@Getter
@RequiredArgsConstructor
public class KeywordScores {

  private final List<KeywordScore> values;

  public static KeywordScores of(List<KeywordScore> values) {
    return new KeywordScores(values);
  }

  public static List<KeywordScore> percentScale(List<KeywordScore> values) {
    return KeywordScores.of(values).keywordScoresPercentScale().entrySet().stream()
        .map(entry -> KeywordScore.of(entry.getKey(), entry.getValue()))
        .toList();
  }

  private Map<Keyword, BigDecimal> keywordScoresPercentScale() {
    Map<Keyword, BigDecimal> totalScores = calculateTotalScoresByEachKeyword();
    BigDecimal maxScore = getMaxScore();
    BigDecimal minScore = getMinScore();
    BigDecimal adjustedScore =
        maxScore.subtract(minScore).divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
    BigDecimal denominator = maxScore.subtract(minScore).add(adjustedScore);

    if (denominator.compareTo(BigDecimal.ZERO) == 0) {
      return totalScores.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> BigDecimal.valueOf(100)));
    }

    return totalScores.entrySet().stream()
        .map(
            entry -> {
              Keyword key = entry.getKey();
              BigDecimal numerator = entry.getValue().subtract(minScore).add(adjustedScore);
              BigDecimal normalizedScore = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
              BigDecimal percentageScaleValue = convertPercentValue(normalizedScore);
              return Map.entry(key, percentageScaleValue);
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private BigDecimal convertPercentValue(BigDecimal normalizedScore) {
    return normalizedScore.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
  }

  private Map<Keyword, BigDecimal> calculateTotalScoresByEachKeyword() {
    return values.stream()
        .collect(
            Collectors.groupingBy(
                KeywordScore::getKeyword,
                Collectors.reducing(BigDecimal.ZERO, KeywordScore::getScore, BigDecimal::add)));
  }

  private BigDecimal getMaxScore() {
    double max =
        values.stream()
            .map(KeywordScore::getScore)
            .mapToDouble(BigDecimal::doubleValue)
            .max()
            .orElseThrow(() -> CustomException.INVALID_KEYWORD_SCORE);
    return BigDecimal.valueOf(max);
  }

  private BigDecimal getMinScore() {
    double min =
        values.stream()
            .map(KeywordScore::getScore)
            .mapToDouble(BigDecimal::doubleValue)
            .min()
            .orElseThrow(() -> CustomException.INVALID_KEYWORD_SCORE);
    return BigDecimal.valueOf(min);
  }
}
