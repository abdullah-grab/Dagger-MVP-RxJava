package com.oneous.currencyconverter.presentation;

public interface Presenter<T> {
    void setView(T view);
    void convert(double amount);
    void clearMemoryAndDiskCache();
}
