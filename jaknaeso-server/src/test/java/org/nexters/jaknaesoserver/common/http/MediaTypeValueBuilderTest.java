package org.nexters.jaknaesoserver.common.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.http.MediaTypeValueBuilder;

public class MediaTypeValueBuilderTest {

  @DisplayName("MediaType 값을 charset과 함께 직렬화할 수 있다")
  @Test
  void serializeMediaTypeWithCharset() {
    String actual1 = MediaTypeValueBuilder.builder(APPLICATION_JSON_VALUE).charset("utf-8").build();
    String actual2 =
        MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE).charset("utf-8").build();

    assertAll(
        () -> assertThat(actual1).isEqualTo("application/json;charset=utf-8"),
        () -> assertThat(actual2).isEqualTo("application/x-www-form-urlencoded;charset=utf-8"));
  }
}
