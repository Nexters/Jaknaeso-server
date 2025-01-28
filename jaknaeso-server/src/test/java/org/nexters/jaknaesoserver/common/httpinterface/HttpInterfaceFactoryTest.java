package org.nexters.jaknaesoserver.common.httpinterface;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.httpinterface.HttpInterfaceFactory;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesoserver.common.httpinterface.fixture.HttpInterfaceFixture.EmptyUrlHttpInterface;
import org.nexters.jaknaesoserver.common.httpinterface.fixture.HttpInterfaceFixture.NoHttpExchangeHttpInterface;

class HttpInterfaceFactoryTest {

  HttpInterfaceFactory httpInterfaceFactory = new HttpInterfaceFactory();

  @DisplayName("HttpInterface에 적절한 프록시 객체를 생성할 수 있다.")
  @Test
  void createHttpInterface() {
    assertDoesNotThrow(
        () -> httpInterfaceFactory.create(KakaoClient.class)
    );
  }

  @DisplayName("HttpExchange 애노테이션이 없는 HttpInterface는 프록시 객체를 생성할 수 없다.")
  @Test
  void failToCreateHttpInterfaceWithoutHttpExchange() {
    assertThatThrownBy(
        () -> httpInterfaceFactory.create(NoHttpExchangeHttpInterface.class))
        .isInstanceOf(IllegalStateException.class);
  }

  @DisplayName("HttpExchange 애노테이션의 url 값이 비어있는 HttpInterface는 프록시 객체를 생성할 수 없다.")
  @Test
  void failToCreateHttpInterfaceWithoutUrl() {
    assertThatThrownBy(() -> httpInterfaceFactory.create(EmptyUrlHttpInterface.class))
        .isInstanceOf(IllegalArgumentException.class);
  }
}