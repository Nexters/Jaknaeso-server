package org.nexters.jaknaesocore.domain.auth.restclient;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.common.support.MediaTypeValueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

class KakaoClientTest extends IntegrationTest {

  @Autowired KakaoClient kakaoClient;

  @Test
  void Authorization_헤더_없이_카카오_API를_호출하면_RestClientException을_반환한다() {
    String mediaType =
        MediaTypeValueBuilder.builder(APPLICATION_FORM_URLENCODED_VALUE).charset("utf-8").build();

    thenThrownBy(() -> kakaoClient.requestUserInfo("")).isInstanceOf(RestClientException.class);
  }
}
