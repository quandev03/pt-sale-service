package com.vnsky.bcss.projectbase;

import com.vnsky.bcss.projectbase.shared.generation.domain.ExcludeFromGeneratedCodeCoverage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
@ExcludeFromGeneratedCodeCoverage(reason = "Not testing logs")
@ComponentScan(basePackages = {"com.vnsky"})
public class ProjectBaseApp {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(ProjectBaseApp.class, args).getEnvironment();

        if (log.isInfoEnabled()) {
            log.info(ApplicationStartupTraces.of(env));
        }
    }
}
