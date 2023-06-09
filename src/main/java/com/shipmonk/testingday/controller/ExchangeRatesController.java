package com.shipmonk.testingday.controller;

import com.shipmonk.testingday.service.RateExchangeService;
import com.shipmonk.testingday.domain.ResponseRate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    path = "/api/v1/rates"
)
public class ExchangeRatesController
{
    @Value("${FIXER_API_ACCESS_KEY}")
    private String accessKey;

    private final RateExchangeService rateExchangeService;

    public ExchangeRatesController(RateExchangeService rateExchangeService)
    {
        this.rateExchangeService = rateExchangeService;
    }
    @RequestMapping(method = RequestMethod.GET, path = "/{day}")
    public ResponseEntity<ResponseRate> getRates(@PathVariable("day") String day)
    {
        ResponseRate ratesMono = rateExchangeService.getExchangeData(day, accessKey);

        return ratesMono != null
            ? new ResponseEntity<>(ratesMono, HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
