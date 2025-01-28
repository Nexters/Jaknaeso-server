package org.nexters.jaknaesocore.domain.auth.restclient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.http.support.MediaTypeValueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;

@SpringBootTest
class KakaoClientTest {

  @Autowired KakaoClient kakaoClient;

  @DisplayName("Authorization 헤더 없이 카카오 API를 호출하면 RestClientException을 반환한다.")
  @Test
  void failToRequestUserInfoWithoutAuthorization() {
    String mediaType =
        MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE).charset("utf-8").build();

    assertThatThrownBy(() -> kakaoClient.requestUserInfo("", mediaType))
        .isInstanceOf(RestClientException.class);
  }
}
