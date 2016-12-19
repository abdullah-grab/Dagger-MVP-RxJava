package com.oneous.currencyconverter.domain;

public interface Clock {
    long millis();

    Clock REAL = System::currentTimeMillis;
}
