package com.shipmonk.testingday.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.shipmonk.testingday.domain.ResponseRate;
import com.shipmonk.testingday.exception.RemoteSericeNotAvailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class FixerApiClient implements ExchangeRate {
    private final WebClient webClient;

    private final Cache<String, ResponseRate> cache;


    public ResponseRate getExchangeRates(String date, String accessKey) {
        //get data from service
        try {
            ResponseRate responseRate = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(date)
                    .queryParam("access_key", accessKey)
                    .queryParam("base", "EUR")
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new RemoteSericeNotAvailableException(errorBody))))
                .bodyToMono(ResponseRate.class)
                .onErrorResume(Mono::error)
                .retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS))
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new RemoteSericeNotAvailableException(retrySignal.failure())))
                .block();
            if (responseRate != null)
                cache.put(date, responseRate);
            return responseRate;
        } catch (RemoteSericeNotAvailableException e) {
            return recover(e, date, accessKey);
        }
    }

    @Override
    public ResponseRate recover(RemoteSericeNotAvailableException e, String date, String accessKey) {
        return cache.getIfPresent(date);
    }
}
