package com.oneous.currencyconverter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterFactory;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.oneous.currencyconverter.data.ConversionService;
import com.oneous.currencyconverter.domain.Clock;
import com.oneous.currencyconverter.domain.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Singleton
    ConversionService provideConversionService(RequestQueue requestQueue) {
        return new ConversionService(requestQueue);
    }

    @Provides
    @Singleton
    SchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.DEFAULT;
    }

    @Provides
    @Singleton
    Clock provideClock() {
        return Clock.REAL;
    }

    @Provides
    @Singleton
    RequestQueue provideRequestQueue() {
        return Volley.newRequestQueue(app);
    }

    @Provides
    @Nullable
    LayoutInflaterFactory provideLayoutInflaterFactory() {
        return null;
    }
}
