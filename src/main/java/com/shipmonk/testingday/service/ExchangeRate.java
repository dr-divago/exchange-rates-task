package com.shipmonk.testingday.service;

import com.shipmonk.testingday.domain.ResponseRate;
import com.shipmonk.testingday.exception.RemoteSericeNotAvailableException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.sql.SQLException;

public interface ExchangeRate {

    ResponseRate getExchangeRates(String date, String accessKey);

    ResponseRate recover(RemoteSericeNotAvailableException e, String date, String accessKey);

}
