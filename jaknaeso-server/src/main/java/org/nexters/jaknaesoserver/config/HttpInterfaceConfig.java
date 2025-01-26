package org.nexters.jaknaesoserver.config;

import java.util.Set;
import org.nexters.jaknaesoserver.common.httpinterface.HttpInterfaceFactory;
import org.nexters.jaknaesoserver.common.httpinterface.HttpInterfaceFactoryBeanPostProcessor;
import org.nexters.jaknaesoserver.common.httpinterface.HttpInterfaceFinder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpInterfaceConfig {

  @Bean
  public HttpInterfaceFactoryBeanPostProcessor httpInterfaceFactoryBeanPostProcessor
      (final ApplicationContext context, final HttpInterfaceFactory httpInterfaceFactory) {
    Set<BeanDefinition> beanDefinitions = findHttpInterfaceBeanDefinitions(
        findBasePackage(context), context
    );
    return new HttpInterfaceFactoryBeanPostProcessor(httpInterfaceFactory, beanDefinitions);
  }

  private String findBasePackage(final ApplicationContext context) {
    return context.getBeansWithAnnotation(SpringBootApplication.class)
        .values()
        .stream()
        .map(it -> it.getClass().getPackage().getName())
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("SpringBootApplication 애노테이션이 붙은 클래스가 존재하지 않습니다."));
  }

  Set<BeanDefinition> findHttpInterfaceBeanDefinitions(
      final String basePackage, final ApplicationContext context
  ) {
    return HttpInterfaceFinder.findBeanDefinitions(basePackage, context.getEnvironment());
  }
}
