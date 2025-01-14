package org.nexters.jaknaeso.common.log;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P6SpyConfig {

    @Bean
    public P6SpyFormatter p6SpyFormatter() {
        return new P6SpyFormatter();
    }

}
