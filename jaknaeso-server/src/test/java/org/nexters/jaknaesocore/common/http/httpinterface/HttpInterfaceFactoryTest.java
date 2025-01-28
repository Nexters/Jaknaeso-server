package org.nexters.jaknaesocore.common.http.httpinterface;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.http.httpinterface.fixture.HttpInterfaceFixture.EmptyUrlHttpInterface;
import org.nexters.jaknaesocore.common.http.httpinterface.fixture.HttpInterfaceFixture.NoHttpExchangeHttpInterface;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;

class HttpInterfaceFactoryTest {

  HttpInterfaceFactory httpInterfaceFactory = new HttpInterfaceFactory();

  @DisplayName("HttpInterface를 위한 프록시 객체를 생성한다.")
  @Test
  void createHttpInterface() {
    assertDoesNotThrow(() -> httpInterfaceFactory.create(KakaoClient.class));
  }

  @DisplayName("HttpExchange 애노테이션이 없는 HttpInterface는 프록시 객체 생성에 실패한다.")
  @Test
  void failToCreateHttpInterfaceWithoutHttpExchange() {
    assertThatThrownBy(() -> httpInterfaceFactory.create(NoHttpExchangeHttpInterface.class))
        .isInstanceOf(IllegalStateException.class);
  }

  @DisplayName("HttpExchange 애노테이션의 url 값이 비어있는 HttpInterface는 프록시 객체 생성에 실패한다.")
  @Test
  void failToCreateHttpInterfaceWithoutUrl() {
    assertThatThrownBy(() -> httpInterfaceFactory.create(EmptyUrlHttpInterface.class))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
