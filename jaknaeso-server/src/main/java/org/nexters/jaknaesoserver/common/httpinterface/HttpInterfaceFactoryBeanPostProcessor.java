package org.nexters.jaknaesoserver.common.httpinterface;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class HttpInterfaceFactoryBeanPostProcessor implements BeanFactoryPostProcessor {

  private final HttpInterfaceFactory httpInterfaceFactory;
  private final Set<BeanDefinition> beanDefinitions;

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    beanDefinitions.stream()
        .filter(it -> StringUtils.hasText(it.getBeanClassName()))
        .forEach(it -> registerSingletonBean(beanFactory, it));
  }

  private void registerSingletonBean(
      final ConfigurableListableBeanFactory beanFactory,
      final BeanDefinition beanDefinition
  ) {
    beanFactory.registerSingleton(
        beanDefinition.getBeanClassName(),
        createHttpInterfaceProxy(beanDefinition)
    );
  }

  private Object createHttpInterfaceProxy(final BeanDefinition beanDefinition) {
    return httpInterfaceFactory.create(findHttpInterface(beanDefinition));
  }

  private Class<?> findHttpInterface(final BeanDefinition beanDefinition) {
    try {
      return ClassUtils.forName(
          beanDefinition.getBeanClassName(), this.getClass().getClassLoader()
      );
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }
}
