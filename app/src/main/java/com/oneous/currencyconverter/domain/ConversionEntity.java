package com.oneous.currencyconverter.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ConversionEntity implements Parcelable {
    public abstract double value();

    public abstract long timestamp();

    public static ConversionEntity create(double value, long timestamp) {
        return new AutoValue_ConversionEntity(value, timestamp);
    }
}
