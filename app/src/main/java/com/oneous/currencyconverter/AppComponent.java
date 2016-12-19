package com.oneous.currencyconverter;

import com.oneous.currencyconverter.ui.ConversionFragment;
import com.oneous.currencyconverter.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    MainActivity inject(MainActivity activity);

    ConversionFragment inject(ConversionFragment fragment);
}
