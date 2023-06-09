package com.shipmonk.testingday.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.shipmonk.testingday.domain.ResponseRate;
import com.shipmonk.testingday.repository.ExchangeRepo;
import com.shipmonk.testingday.repository.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class RateExchangeService {

    private final FixerApiClient fixerApiClient;

    private final ExchangeRepo exchangeRepository;

    private final Cache<String, ResponseRate> cache;

    public RateExchangeService(FixerApiClient fixerApiClient, ExchangeRepo exchangeRepository, Cache<String, ResponseRate> cache) {
        this.fixerApiClient = fixerApiClient;
        this.exchangeRepository = exchangeRepository;
        this.cache = cache;
    }
    public ResponseRate getExchangeData(String data, String accessKey) {
        //is in cache?
        if (cache.getIfPresent(data) != null) {
            return cache.getIfPresent(data);
        }
        ResponseRate responseRate =  fixerApiClient.getExchangeRates(data, accessKey);
        if (responseRate == null) {
            throw new RuntimeException("No data found");
        }
        ResponseEntity entity = mapResponseToEntity(responseRate);
        exchangeRepository.save(entity);
        cache.put(data, responseRate);
        return responseRate;
    }

    private ResponseEntity mapResponseToEntity(ResponseRate responseRate) {
        ResponseEntity entity = new ResponseEntity();
        entity.setSuccess(responseRate.success());
        entity.setTimestamp(responseRate.timestamp());
        entity.setHistorical(responseRate.historical());
        entity.setBase(responseRate.base());
        entity.setDate(responseRate.date());
        return entity;
    }
}
