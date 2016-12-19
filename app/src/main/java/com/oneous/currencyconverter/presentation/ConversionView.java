package com.oneous.currencyconverter.presentation;

public interface ConversionView {
    void display(double totalAmount, double conversionRate);

    void showLoading();

    void hideLoading();
}
