package org.nexters.jaknaesoserver.common.httpinterface;

import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.service.annotation.HttpExchange;

public class HttpInterfaceFinder {

  public static Set<BeanDefinition> findBeanDefinitions(
      final String basePackage, final Environment environment
  ) {
    var scanner = new ClassPathScanningCandidateComponentProvider(false, environment) {

      @Override
      protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata beanMetadata = beanDefinition.getMetadata();
        try {
          Class<?> clazz = Class.forName(beanMetadata.getClassName());

          return beanMetadata.isInterface()
              && beanMetadata.hasAnnotation(HttpExchange.class.getName())
              && !java.lang.reflect.Modifier.isPrivate(clazz.getModifiers());
        } catch (ClassNotFoundException e) {
          return false;
        }
      }
    };

    scanner.addIncludeFilter(new AnnotationTypeFilter(HttpExchange.class));
    return scanner.findCandidateComponents(basePackage);
  }
}
