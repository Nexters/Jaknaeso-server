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

  /**
   * 1. 내가 선택한 옵션의 keywordScore를 모두 가져온다. 2. 해당 점수의 키워드별 합산 점수를 구한다. 2-1. 해당 예시의 경우 [(성공, 2) , (안전,
   * -1), (보편, 3)] 3. 상수를 도입하여 선택한 값에 대해 보정할 수 있도록 추가한다 3-1. 상수는 (최대 - 최소) / 10 으로 설정한다. 4. 0~1 값으로
   * 점수를 조정한다 (min-max 정규화라고 하네요) 4-1. 계산 식 : 키워드의 정규화 값 = (키워드의 점수 - 점수의 최소값 + 상수) / (최대값 - 최소값 +
   * 상수) 4-2. 계산 식 예시 성공 : (2 - (-1) + 0.4)/3-(-1) + 0.4 4-3. 결과 : [(성공, 3.4/4.4), (안전, 0.4/4), (보편,
   * 4.4/4.4)] 5. 해당 값을 0~100 으로 스케일 하기 5-1. 결과값 * 100
   */
  public static List<KeywordScore> percentScale(List<KeywordScore> values) {
    return KeywordScores.of(values).keywordScoresPercentScale().entrySet().stream()
        .map(entry -> KeywordScore.of(entry.getKey(), entry.getValue()))
        .toList();
  }

  private Map<Keyword, BigDecimal> keywordScoresPercentScale() {
    Map<Keyword, BigDecimal> sumScores = getSumScores();
    BigDecimal maxScore = getMaxScore(); // 최댓값 계산
    BigDecimal minScore = getMinScore(); // 최솟값 계산
    BigDecimal adjustedScore =
        maxScore
            .subtract(minScore)
            .divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP); // 상수: (최대 - 최소) / 10
    BigDecimal denominator = maxScore.subtract(minScore).add(adjustedScore); // 분모: (최대 - 최소 + 상수)

    if (denominator.compareTo(BigDecimal.ZERO) == 0) {
      return sumScores.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> BigDecimal.valueOf(100)));
    }

    return sumScores.entrySet().stream()
        .map(
            entry -> {
              Keyword key = entry.getKey();
              BigDecimal numerator =
                  entry.getValue().subtract(minScore).add(adjustedScore); // 분자: (점수 - 최소 + 상수)
              BigDecimal normalizedScore =
                  numerator.divide(denominator, 2, RoundingMode.HALF_UP); // 정규화 점수: (분자 / 분모)
              BigDecimal percentageScaleValue =
                  normalizedScore
                      .multiply(BigDecimal.valueOf(100))
                      .setScale(2, RoundingMode.HALF_UP); // 백분율 변환 점수: 정규화된 점수 * 100
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
