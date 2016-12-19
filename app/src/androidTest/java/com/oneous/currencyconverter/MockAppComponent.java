package com.oneous.currencyconverter;

import com.oneous.currencyconverter.data.ConversionService;

import javax.inject.Singleton;

import dagger.Component;
import rx.schedulers.TestScheduler;

@Singleton
@Component(modules = MockAppModule.class)
public interface MockAppComponent extends AppComponent {
    ConversionService getConversionService();

    TestScheduler getTestScheduler();
}
