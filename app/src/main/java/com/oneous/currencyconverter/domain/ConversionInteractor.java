package com.oneous.currencyconverter.domain;

import com.oneous.currencyconverter.data.ConversionDiskCache;
import com.oneous.currencyconverter.data.ConversionService;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscription;
import rx.subjects.ReplaySubject;

@Singleton
public class ConversionInteractor {
    private static final long STALE_MS = 15 * 1000;

    private final SchedulerProvider schedulerProvider;
    private final ConversionDiskCache diskCache;
    private final ConversionService networkService;
    private final Clock clock;

    private ConversionEntity memoryCache;
    private ReplaySubject<ConversionEntity> conversionSubject;
    private Subscription conversionSubscription;

    @Inject
    ConversionInteractor(
            SchedulerProvider schedulerProvider,
            ConversionDiskCache diskCache,
            ConversionService networkService,
            Clock clock) {
        this.schedulerProvider = schedulerProvider;
        this.diskCache = diskCache;
        this.networkService = networkService;
        this.clock = clock;
    }

    // http://blog.bradcampbell.nz/keep-your-main-thread-synchronous/
    public Observable<ConversionEntity> value() {
        if (conversionSubscription == null || conversionSubscription.isUnsubscribed()) {
            conversionSubject = ReplaySubject.create();

            conversionSubscription = Observable.concat(memory(), disk(), network())
                    .first(entity -> entity != null && isUpToDate(entity))
                    .subscribe(conversionSubject);
        }

        return conversionSubject.asObservable();
    }

    public void clearMemoryCache() {
        memoryCache = null;
    }

    public void clearMemoryAndDiskCache() {
        diskCache.clear();
        clearMemoryCache();
    }

    private Observable<ConversionEntity> network() {
        return networkService.getValue()
                .map(data -> ConversionEntity.create(data, clock.millis()))
                .doOnNext(entity -> memoryCache = entity)
                .flatMap(entity -> diskCache.saveEntity(entity).map(__ -> entity))
                .compose(schedulerProvider.applySchedulers());
    }

    private Observable<ConversionEntity> disk() {
        return diskCache.getEntity()
                .doOnNext(entity -> memoryCache = entity)
                .compose(schedulerProvider.applySchedulers());
    }

    private Observable<ConversionEntity> memory() {
        return Observable.just(memoryCache);
    }

    private boolean isUpToDate(ConversionEntity entity) {
        return clock.millis() - entity.timestamp() < STALE_MS;
    }
}
