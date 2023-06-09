package com.shipmonk.testingday.domain;



public record ResponseRate(Boolean success, Long timestamp, Boolean historical, String base, String date, Rates rates) {}
