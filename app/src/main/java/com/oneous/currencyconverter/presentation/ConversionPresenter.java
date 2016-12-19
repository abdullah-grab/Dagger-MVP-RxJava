package com.oneous.currencyconverter.presentation;

import com.oneous.currencyconverter.domain.ConversionInteractor;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class ConversionPresenter implements Presenter<ConversionView> {
    private Subscription subscription = Subscriptions.empty();
    private ConversionView view;

    private final ConversionInteractor interactor;

    @Inject
    ConversionPresenter(ConversionInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(ConversionView view) {
        this.view = view;
        if (view == null) {
            subscription.unsubscribe();
        } else {
            // do nothing
        }
    }

    @Override
    public void convert(double amount) {
        view.showLoading();
        subscription = interactor.value().subscribe(
                conversionEntity -> view.display(amount * conversionEntity.value(), conversionEntity.value()),
                err -> view.hideLoading(),
                view::hideLoading);
    }

    /**
     * This is for demo purposes only. It would be strange to expose cache clearing methods
     * to the UI. Typically caches would be cleared as a result of some action, not from
     * pressing a button.
     */
    public void clearMemoryCache() {
        interactor.clearMemoryCache();
    }

    @Override
    public void clearMemoryAndDiskCache() {
        interactor.clearMemoryAndDiskCache();
    }
}
