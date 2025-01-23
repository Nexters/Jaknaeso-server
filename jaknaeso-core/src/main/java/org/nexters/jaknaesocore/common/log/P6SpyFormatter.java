package org.nexters.jaknaesocore.common.log;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.util.Arrays;

public class P6SpyFormatter implements MessageFormattingStrategy {

  @Override
  public String formatMessage(
      int connectionId,
      String now,
      long elapsed,
      String category,
      String prepared,
      String sql,
      String url) {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    String callStack = findCallingClass(stackTrace);

    if ("commit".equalsIgnoreCase(category) || "rollback".equalsIgnoreCase(category)) {
      return String.format(
          "\n\n ======================================== 트랜잭션 정보 ========================================\n"
              + "  # 시간: %s\n"
              + "  # 동작: %s\n"
              + "  # 호출 위치: %s\n"
              + " ===============================================================================================\n",
          now, category, callStack);
    }

    return String.format(
        "\n\n ======================================== P6Spy SQL 로그 ========================================\n"
            + "  # 요청 시간: %s\n"
            + "  # 실행 시간: %dms\n"
            + "  # 카테고리: %s\n"
            + "  # 호출 위치: %s\n"
            + "  # SQL: \n%s\n"
            + " ===============================================================================================\n",
        now, elapsed, category, callStack, formatSql(sql));
  }

  private String findCallingClass(StackTraceElement[] stackTrace) {
    return Arrays.stream(stackTrace)
        .filter(
            element ->
                element.getClassName().contains("org.nexters")
                    && !element.getClassName().contains("P6SpyFormatter")
                    && !element.getClassName().contains("$Proxy"))
        .findFirst()
        .map(element -> element.getClassName() + "." + element.getMethodName())
        .orElse("Unknown");
  }

  private String formatSql(String sql) {
    if (sql == null || sql.trim().isEmpty()) {
      return sql;
    }
    String formattedSql = SqlFormatter.format(sql);

    return Arrays.stream(formattedSql.split("\n"))
        .map(line -> "\t\t" + line) // 들여쓰기 추가
        .reduce((a, b) -> a + "\n" + b)
        .orElse("");
  }
}
