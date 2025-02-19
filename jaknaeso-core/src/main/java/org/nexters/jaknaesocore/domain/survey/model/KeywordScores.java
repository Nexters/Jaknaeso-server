package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeywordScores {

  @Getter private final List<KeywordScore> values;

  public static KeywordScores of(List<Survey> values) {
    return new KeywordScores(
        values.stream()
            .flatMap(survey -> survey.getOptions().stream())
            .flatMap(option -> option.getScores().stream())
            .toList());
  }

  public static List<KeywordScore> percentScale2(List<KeywordScore> values) {
    KeywordScores keywordScores = new KeywordScores(values);
    return keywordScores.keywordScoresPercentScale().entrySet().stream()
        .map(
            entry -> KeywordScore.builder().keyword(entry.getKey()).score(entry.getValue()).build())
        .toList();
  }

  public static List<KeywordScore> percentScale(List<Survey> values) {
    KeywordScores keywordScores =
        new KeywordScores(
            values.stream()
                .flatMap(survey -> survey.getOptions().stream())
                .flatMap(option -> option.getScores().stream())
                .toList());
    return keywordScores.keywordScoresPercentScale().entrySet().stream()
        .map(
            entry -> KeywordScore.builder().keyword(entry.getKey()).score(entry.getValue()).build())
        .toList();
  }

  private Map<Keyword, BigDecimal> keywordScoresPercentScale() {
    /*
     * 1. 내가 선택한 옵션의 keywordScore를 모두 가져온다.
     * 2. 해당 점수의 키워드별 합산 점수를 구한다.
     *    1. 해당 예시의 경우 [(성공, 2) , (안전, -1), (보편, 3)]
     * 3. 0~1 값으로 점수를 조정한다 (min-max 정규화라고 하네요)
     *    1. 계산 식 : 키워드의 정규화 값 = (키워드의 점수 - 점수의 최소값) / (최대값 - 최소값)
     *    2. 계산 식 예시 성공 : (2 - (-1))/3-(-1)
     *    3. 결과 : [(성공, 3/4), (안전, 0/4), (보편, 4/4)]
     * 4. 해당 값을 0~100 으로 스케일 하기
     *    1. 단순히 결과값 * 100
     * */
    Map<Keyword, BigDecimal> sumScores = getSumScores();
    BigDecimal maxScore = getMaxScore(); // 최댓값 계산
    BigDecimal minScore = getMinScore(); // 최솟값 계산
    BigDecimal denominator = maxScore.subtract(minScore); // 분모: (최대 - 최소)

    if (denominator.compareTo(BigDecimal.ZERO) == 0) {
      return sumScores.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> BigDecimal.valueOf(100)));
    }

    return sumScores.entrySet().stream()
        .map(
            entry -> {
              Keyword key = entry.getKey();
              BigDecimal numerator = entry.getValue().subtract(minScore); // 분자: (점수 - 최소)
              BigDecimal normalizedScore =
                  numerator.divide(denominator, 2, RoundingMode.HALF_UP); // (분자 / 분모)
              BigDecimal percentageScaleValue =
                  normalizedScore
                      .multiply(BigDecimal.valueOf(100))
                      .setScale(2, RoundingMode.HALF_UP); // 정규화된 점수 * 100
              return Map.entry(key, percentageScaleValue);
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<Keyword, BigDecimal> getSumScores() {
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
            .orElseThrow(() -> new IllegalArgumentException("No max value"));
    return BigDecimal.valueOf(max);
  }

  private BigDecimal getMinScore() {
    double min =
        values.stream()
            .map(KeywordScore::getScore)
            .mapToDouble(BigDecimal::doubleValue)
            .min()
            .orElseThrow(() -> new IllegalArgumentException("No min value"));
    return BigDecimal.valueOf(min);
  }
}
