package org.nexters.jaknaesocore.common.http.httpinterface.fixture;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

public class HttpInterfaceFixture {

  public interface NoHttpExchangeHttpInterface {

    @GetExchange("hello")
    String hello();
  }

  @HttpExchange
  public interface EmptyUrlHttpInterface {

    @GetExchange("hello")
    String hello();
  }
}
