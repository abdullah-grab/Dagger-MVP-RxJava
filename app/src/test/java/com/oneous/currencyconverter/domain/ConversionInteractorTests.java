package com.oneous.currencyconverter.domain;

import com.oneous.currencyconverter.data.ConversionDiskCache;
import com.oneous.currencyconverter.data.ConversionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ConversionInteractorTests {
    @Mock
    ConversionService service;
    @Mock
    ConversionDiskCache cache;
    @Mock
    Clock clock;

    private ConversionInteractor interactor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        SchedulerProvider schedulerProvider = new SchedulerProvider() {
            @Override
            public <T> Observable.Transformer<T, T> applySchedulers() {
                return observable -> observable;
            }
        };

        when(cache.saveEntity(any())).thenReturn(Observable.just(null));

        interactor = new ConversionInteractor(schedulerProvider, cache, service, clock);
    }

    @Test
    public void testHitsMemoryCache() {
        ConversionEntity expectedResult = ConversionEntity.create(1, 0L);
        ConversionEntity nonExpectedResult = ConversionEntity.create(2, 0L);

        when(service.getValue()).thenReturn(Observable.just(1.0));
        when(cache.getEntity()).thenReturn(Observable.just(null));
        when(clock.millis()).thenReturn(0L);

        TestSubscriber<ConversionEntity> testSubscriberFirst = new TestSubscriber<>();
        interactor.value().subscribe(testSubscriberFirst);
        testSubscriberFirst.assertNoErrors();
        testSubscriberFirst.assertReceivedOnNext(singletonList(expectedResult));

        when(cache.getEntity()).thenReturn(Observable.just(nonExpectedResult));
        when(service.getValue()).thenReturn(Observable.just(2.0));

        TestSubscriber<ConversionEntity> testSubscriberSecond = new TestSubscriber<>();
        interactor.value().subscribe(testSubscriberSecond);
        testSubscriberSecond.assertNoErrors();
        testSubscriberSecond.assertReceivedOnNext(singletonList(expectedResult));
    }

    @Test
    public void testHitsDiskCache() {
        ConversionEntity expectedResult = ConversionEntity.create(1, 0L);

        when(service.getValue()).thenReturn(Observable.just(1.0));
        when(cache.getEntity()).thenReturn(Observable.just(null));
        when(clock.millis()).thenReturn(0L);

        TestSubscriber<ConversionEntity> testSubscriberFirst = new TestSubscriber<>();
        interactor.value().subscribe(testSubscriberFirst);
        testSubscriberFirst.assertNoErrors();
        testSubscriberFirst.assertReceivedOnNext(singletonList(expectedResult));

        interactor.clearMemoryCache();
        when(cache.getEntity()).thenReturn(Observable.just(expectedResult));
        when(service.getValue()).thenReturn(Observable.just(2.0));

        TestSubscriber<ConversionEntity> testSubscriberSecond = new TestSubscriber<>();
        interactor.value().subscribe(testSubscriberSecond);
        testSubscriberSecond.assertNoErrors();
        testSubscriberSecond.assertReceivedOnNext(singletonList(expectedResult));
    }

    @Test
    public void testCacheExpiry() {
        ConversionEntity expectedResultFirst = ConversionEntity.create(1, 0L);
        when(service.getValue()).thenReturn(Observable.empty());
        when(cache.getEntity()).thenReturn(Observable.just(expectedResultFirst));
        when(clock.millis()).thenReturn(0L);

        TestSubscriber<ConversionEntity> testSubscriberFirst = new TestSubscriber<>();
        interactor.value().subscribe(testSubscriberFirst);
        testSubscriberFirst.assertNoErrors();
        testSubscriberFirst.assertReceivedOnNext(singletonList(expectedResultFirst));

        when(clock.millis()).thenReturn(4999L);
        TestSubscriber<ConversionEntity> testSubscriberSecond = new TestSubscriber<>();
        interactor.value().subscribe(testSubscriberSecond);
        testSubscriberSecond.assertNoErrors();
        testSubscriberSecond.assertReceivedOnNext(singletonList(expectedResultFirst));

        when(clock.millis()).thenReturn(5000L);
        when(service.getValue()).thenReturn(Observable.just(2.0));

        TestSubscriber<ConversionEntity> testSubscriberThird = new TestSubscriber<>();
        interactor.value().subscribe(testSubscriberThird);
        testSubscriberThird.assertNoErrors();
        testSubscriberThird.assertReceivedOnNext(singletonList(ConversionEntity.create(2, 5000L)));
    }
}