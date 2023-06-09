package com.shipmonk.testingday.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shipmonk.testingday.domain.ResponseRate;
import com.shipmonk.testingday.exception.RemoteSericeNotAvailableException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableRetry
public class AppConfig {
    private final String baseUrl = "http://data.fixer.io/api/";
    @Bean
    Cache<String, ResponseRate> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(500)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .weakKeys()
            .recordStats().build();
    }

    @Bean
    WebClient webClient() {
        return WebClient
            .builder()
            .filter(errorHandler())
            .baseUrl(baseUrl)
            .build();
    }


    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError() || clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody -> Mono.error(new RemoteSericeNotAvailableException(errorBody)));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
}
