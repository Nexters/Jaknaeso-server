package org.nexters.jaknaesocore.common.http.httpinterface;

import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.http.httpinterface.fixture.HttpInterfaceFixture.EmptyUrlHttpInterface;
import org.nexters.jaknaesocore.common.http.httpinterface.fixture.HttpInterfaceFixture.NoHttpExchangeHttpInterface;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;

class HttpInterfaceFactoryTest {

  HttpInterfaceFactory httpInterfaceFactory = new HttpInterfaceFactory();

  @Test
  void HttpInterface를_위한_프록시_객체를_생성한다() {
    thenCode(() -> httpInterfaceFactory.create(KakaoClient.class)).doesNotThrowAnyException();
  }

  @Test
  void HttpExchange_애노테이션이_없는_HttpInterface는_프록시_객체_생성에_실패한다() {
    thenThrownBy(() -> httpInterfaceFactory.create(NoHttpExchangeHttpInterface.class))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void HttpExchange_애노테이션의_url_값이_비어있는_HttpInterface는_프록시_객체_생성에_실패한다() {
    thenThrownBy(() -> httpInterfaceFactory.create(EmptyUrlHttpInterface.class))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
