package org.nexters.jaknaesocore.common.http.httpinterface;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class HttpInterfaceFinderTest {

  @Test
  void 현재_클래스가_존재하는_패키지에서_HttpExchange_애노테이션이_있는_HttpInterface_BeanDefinition을_찾는다() {
    Set<BeanDefinition> actual =
        HttpInterfaceFinder.findBeanDefinitions(
            this.getClass().getPackage().getName(),
            new AnnotationConfigApplicationContext().getEnvironment());

    then(actual.size()).isEqualTo(1);
  }
}
