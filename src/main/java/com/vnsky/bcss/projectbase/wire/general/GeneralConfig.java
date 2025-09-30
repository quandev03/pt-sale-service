package com.vnsky.bcss.projectbase.wire.general;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vnsky.common.rest.ProxyCustomizer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Configuration
public class GeneralConfig {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @SneakyThrows
    @Bean
    public ProxyCustomizer proxyCustomizer() {
        return () -> {
            SimpleClientHttpRequestFactory clientHttpRequestFactory  = new SimpleClientHttpRequestFactory();
            clientHttpRequestFactory.setConnectTimeout(30000);
            clientHttpRequestFactory.setReadTimeout(Duration.ofMinutes(3));
            return clientHttpRequestFactory;
        };
    }

    @Bean
    @SneakyThrows
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,ProxyCustomizer proxyCustomizer, RestTemplateCustomizer... restTemplateCustomizers) {
        return restTemplateBuilder
            .requestFactory(proxyCustomizer)
            .additionalCustomizers(restTemplateCustomizers)
            .build();
    }
}
