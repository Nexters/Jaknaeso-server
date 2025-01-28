package org.nexters.jaknaesocore.common.http.support;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MediaTypeValueBuilderTest {

  @DisplayName("MediaType 값을 charset과 함께 직렬화한다.")
  @Test
  void serializeMediaTypeWithCharset() {
    String actual1 = MediaTypeValueBuilder.builder(APPLICATION_JSON_VALUE).charset("utf-8").build();
    String actual2 =
        MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE).charset("utf-8").build();

    assertAll(
        () -> then(actual1).isEqualTo("application/json;charset=utf-8"),
        () -> then(actual2).isEqualTo("application/x-www-form-urlencoded;charset=utf-8"));
  }
}
