package org.nexters.jaknaesoserver.common.httpinterface;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

class HttpInterfaceFinderTest {

  @DisplayName("""
          1) 현재 클래스가 존재하는 패키지에서
          2) HttpExchange 애노테이션이 있고
          3) private 접근 제어자가 붙지 않은
          HttpInterface BeanDefinition을 찾을 수 있다.
      """)
  @Test
  void findHttpInterfaceBeanDefinitions() {
    Set<BeanDefinition> actual = HttpInterfaceFinder.findBeanDefinitions(
        this.getClass().getPackage().getName(),
        new AnnotationConfigApplicationContext().getEnvironment()
    );

    assertThat(actual.size()).isEqualTo(1);
  }

  @HttpExchange
  interface ValidHttpExchangeInterface {

    @GetExchange("hello")
    String hello();
  }

  interface InvalidHttpExchangeInterface {

    @GetExchange("hello")
    String hello();
  }

  @HttpExchange
  private interface PrivateHttpInterface {

    @GetExchange("hello")
    String hello();
  }
}