package io.locngo.learning.microservices.review.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "io.locngo.learning.microservices")
public class ReviewApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewApplication.class);

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(ReviewApplication.class);

        String mysqlUri = context.getEnvironment().getProperty("spring.datasource.url");
        LOGGER.info("Connected to MYSQL: " + mysqlUri);
    }
}
