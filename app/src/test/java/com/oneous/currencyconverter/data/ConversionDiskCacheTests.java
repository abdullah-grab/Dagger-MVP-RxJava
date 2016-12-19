package com.oneous.currencyconverter.data;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.oneous.currencyconverter.domain.ConversionEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import rx.observers.TestSubscriber;

import static com.oneous.currencyconverter.data.ConversionDiskCache.KEY_DATA;
import static com.oneous.currencyconverter.data.ConversionDiskCache.KEY_TIMESTAMP;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ConversionDiskCacheTests {
    @Mock
    SharedPreferences prefs;

    private ConversionDiskCache cache;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        cache = new ConversionDiskCache(prefs);
    }

    @Test
    public void testEmptyCache() {
        when(prefs.getInt(KEY_DATA, 0)).thenReturn(0);

        TestSubscriber<ConversionEntity> testSubscriber = new TestSubscriber<>();
        cache.getEntity().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(null));
    }

    @Test
    public void testNonEmptyCache() {
        when(prefs.getInt(KEY_DATA, 0)).thenReturn(1);
        when(prefs.getLong(KEY_TIMESTAMP, 0L)).thenReturn(2L);

        ConversionEntity expectedResult = ConversionEntity.create(1, 2L);

        TestSubscriber<ConversionEntity> testSubscriber = new TestSubscriber<>();
        cache.getEntity().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(expectedResult));
    }

    @SuppressLint("CommitPrefEdits")
    @Test
    public void testSavingToDisk() {
        ConversionEntity expectedResult = ConversionEntity.create(1, 2L);

        SharedPreferences.Editor editorMock = mock(SharedPreferences.Editor.class);
        when(prefs.edit()).thenReturn(editorMock);

        class IncrementCountAnswer implements Answer {
            private int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return TRUE;
            }
        }

        IncrementCountAnswer incrementCount = new IncrementCountAnswer();
        doAnswer(incrementCount).when(editorMock).commit();
        doAnswer(incrementCount).when(editorMock).apply();

        when(editorMock.putInt(any(String.class), any(Integer.class))).thenReturn(editorMock);
        when(editorMock.putLong(any(String.class), any(Long.class))).thenReturn(editorMock);

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        cache.saveEntity(expectedResult).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(TRUE));

        verify(editorMock).putInt(KEY_DATA, 1);
        verify(editorMock).putLong(KEY_TIMESTAMP, 2L);

        assertEquals(1, incrementCount.count);
    }

    @SuppressLint("CommitPrefEdits")
    @Test
    public void testClearingCache() {
        SharedPreferences.Editor editorMock = mock(SharedPreferences.Editor.class);
        when(prefs.edit()).thenReturn(editorMock);
        when(editorMock.remove(any(String.class))).thenReturn(editorMock);

        class IncrementCountAnswer implements Answer {
            private int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return TRUE;
            }
        }
        IncrementCountAnswer incrementCount = new IncrementCountAnswer();
        doAnswer(incrementCount).when(editorMock).commit();
        doAnswer(incrementCount).when(editorMock).apply();

        cache.clear();

        verify(editorMock).remove(KEY_DATA);
        verify(editorMock).remove(KEY_TIMESTAMP);

        assertEquals(1, incrementCount.count);
    }
}
