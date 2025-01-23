package org.nexters.jaknaesoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.nexters")
public class JaknaesoserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(JaknaesoserverApplication.class, args);
    }
}
