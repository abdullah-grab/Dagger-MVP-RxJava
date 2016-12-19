package com.oneous.currencyconverter.data;

import android.content.SharedPreferences;

import com.oneous.currencyconverter.domain.ConversionEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class ConversionDiskCache {
    static final String KEY_DATA = "value-xyz-data";
    static final String KEY_TIMESTAMP = "value-xyz-timestamp";

    private SharedPreferences prefs;

    @Inject
    ConversionDiskCache(SharedPreferences sharedPreferences) {
        this.prefs = sharedPreferences;
    }

    public Observable<ConversionEntity> getEntity() {
        return Observable.defer(() -> {
            Observable<ConversionEntity> result;

            if (prefs.contains(KEY_DATA)) {
                double conversionRate = (double) prefs.getFloat(KEY_DATA, 0.0f);
                long timestamp = prefs.getLong(KEY_TIMESTAMP, 0L);
                result = Observable.just(ConversionEntity.create(conversionRate, timestamp));
            } else {
                result = Observable.just(null);
            }
            return result;
        });
    }

    public Observable<Boolean> saveEntity(ConversionEntity value) {
        return Observable.defer(() -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(KEY_DATA, (float) value.value());
            editor.putLong(KEY_TIMESTAMP, value.timestamp());
            return Observable.just(editor.commit());
        });
    }

    public void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_DATA);
        editor.remove(KEY_TIMESTAMP);
        editor.apply();
    }
}
