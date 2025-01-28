package org.nexters.jaknaesoserver.common.httpinterface;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.httpinterface.HttpInterfaceFinder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class HttpInterfaceFinderTest {

  @DisplayName("현재 클래스가 존재하는 패키지에서 HttpExchange 애노테이션이 있는 HttpInterface BeanDefinition을 찾는다.")
  @Test
  void findHttpInterfaceBeanDefinitions() {
    Set<BeanDefinition> actual =
        HttpInterfaceFinder.findBeanDefinitions(
            this.getClass().getPackage().getName(),
            new AnnotationConfigApplicationContext().getEnvironment());

    assertThat(actual.size()).isEqualTo(1);
  }
}
