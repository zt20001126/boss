package com.boss.matching;

import com.boss.matching.config.AppProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Spring Boot entry point for the matching backend application.
 */
@SpringBootApplication
@MapperScan("com.boss.matching.persistence.mapper")
@EnableConfigurationProperties(AppProperties.class)
public class MatchingApplication {
    /**
     * Starts the Spring Boot application.
     * @param args input value
     * @return result value
     */
    public static void main(String[] args) {
        SpringApplication.run(MatchingApplication.class, args);
    }
}
