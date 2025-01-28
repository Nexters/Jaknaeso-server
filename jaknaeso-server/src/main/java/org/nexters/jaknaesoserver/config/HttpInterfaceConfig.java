package org.nexters.jaknaesoserver.config;

import java.util.Set;
import org.nexters.jaknaesocore.common.httpinterface.HttpInterfaceFactory;
import org.nexters.jaknaesocore.common.httpinterface.HttpInterfaceFactoryBeanPostProcessor;
import org.nexters.jaknaesocore.common.httpinterface.HttpInterfaceFinder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpInterfaceConfig {

  @Bean
  public HttpInterfaceFactoryBeanPostProcessor httpInterfaceFactoryBeanPostProcessor(
      final ApplicationContext context, final HttpInterfaceFactory httpInterfaceFactory) {
    Set<BeanDefinition> beanDefinitions =
        findHttpInterfaceBeanDefinitions("org.nexters.jaknaesocore", context);
    return new HttpInterfaceFactoryBeanPostProcessor(httpInterfaceFactory, beanDefinitions);
  }

  Set<BeanDefinition> findHttpInterfaceBeanDefinitions(
      final String basePackage, final ApplicationContext context) {
    return HttpInterfaceFinder.findBeanDefinitions(basePackage, context.getEnvironment());
  }
}
