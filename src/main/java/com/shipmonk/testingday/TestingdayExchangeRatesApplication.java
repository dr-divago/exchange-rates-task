package com.shipmonk.testingday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableJpaRepositories("com.shipmonk.testingday.repository")
public class TestingdayExchangeRatesApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(TestingdayExchangeRatesApplication.class, args);
    }

}
