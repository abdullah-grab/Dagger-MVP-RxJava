package com.oneous.currencyconverter.presentation;

import com.oneous.currencyconverter.domain.ConversionEntity;
import com.oneous.currencyconverter.domain.ConversionInteractor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.schedulers.TestScheduler;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.Observable.just;

@RunWith(RobolectricTestRunner.class)
public class ConversionPresenterTests {
    @Mock
    ConversionInteractor interactor;
    @Mock
    ConversionView view;

    private ConversionPresenter conversionPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        conversionPresenter = new ConversionPresenter(interactor);
    }

    @Test
    public void testLoadingIsCalledCorrectly() {
        TestScheduler testScheduler = new TestScheduler();
        Observable<ConversionEntity> result = just(ConversionEntity.create(0, 0)).subscribeOn(testScheduler);
        when(interactor.value()).thenReturn(result);

        conversionPresenter.setView(view);

        verify(view, times(1)).showLoading();
        verify(view, never()).hideLoading();
        verify(view, never()).display(10.0, 5.0);

        testScheduler.triggerActions();

        verify(view, times(1)).display(10.0, 5.0);
        verify(view, times(1)).hideLoading();
    }
}
