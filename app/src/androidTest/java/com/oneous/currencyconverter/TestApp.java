package com.oneous.currencyconverter;

public class TestApp extends App {
    @Override
    protected AppComponent createComponent() {
        return DaggerMockAppComponent.builder()
                .mockAppModule(new MockAppModule())
                .build();
    }
}
