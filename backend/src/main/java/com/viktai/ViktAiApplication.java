package com.viktai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ViktAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ViktAiApplication.class, args);
    }
}
