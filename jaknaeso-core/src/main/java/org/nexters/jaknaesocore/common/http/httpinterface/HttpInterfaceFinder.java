package org.nexters.jaknaesocore.common.http.httpinterface;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.service.annotation.HttpExchange;

public class HttpInterfaceFinder {

  public static Set<BeanDefinition> findBeanDefinitions(
      final String basePackage, final Environment environment) {
    var scanner =
        new ClassPathScanningCandidateComponentProvider(false, environment) {

          @Override
          protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata beanMetadata = beanDefinition.getMetadata();
            return beanMetadata.isInterface()
                && beanMetadata.hasAnnotation(HttpExchange.class.getName());
          }
        };

    scanner.addIncludeFilter(new AnnotationTypeFilter(HttpExchange.class));
    if (isTestEnvironment()) {
      return scanner.findCandidateComponents(basePackage).stream()
          .filter(HttpInterfaceFinder::filterFixture)
          .collect(Collectors.toSet());
    }
    return scanner.findCandidateComponents(basePackage);
  }

  static boolean filterFixture(final BeanDefinition beanDefinition) {
    try {
      Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
      return !clazz.getPackage().getName().contains("fixture");
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  static boolean isTestEnvironment() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (StackTraceElement element : stackTrace) {
      if (element.getClassName().contains("org.springframework.boot.test.context")) {
        return true;
      }
    }
    return false;
  }
}
