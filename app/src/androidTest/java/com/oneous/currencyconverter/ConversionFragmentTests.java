package com.oneous.currencyconverter;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import com.oneous.currencyconverter.data.ConversionService;
import com.oneous.currencyconverter.ui.ConversionFragment;
import com.oneous.currencyconverter.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.schedulers.TestScheduler;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ConversionFragmentTests {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class, false, false);

    private ConversionService service;
    private TestScheduler testScheduler;

    @Before
    public void setup() throws Throwable {
        // Set up application
        App app = (App) getTargetContext().getApplicationContext();
        MockAppComponent component = (MockAppComponent) App.getAppComponent(app);
        service = component.getConversionService();
        testScheduler = component.getTestScheduler();

        // Launch main activity
        main.launchActivity(MainActivity.getStartIntent(getTargetContext(), false));
    }

    @Test
    public void testLoadingDisplaysCorrectly() throws Throwable {
        when(service.getValue()).thenReturn(Observable.just(0.0));
        setupFragment();

        // Check loading is showing
        onView(withId(R.id.loading)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view)).check(matches(not(isDisplayed())));

        // Trigger onNext/onCompleted
        testScheduler.triggerActions();

        // Check loading is hidden
        onView(withId(R.id.loading)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_view)).check(matches(isDisplayed()));
    }

    private void setupFragment() {
        FragmentManager fragmentManager = main.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.root, new ConversionFragment())
                .commit();

        // Wait for the fragment to be committed
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.waitForIdleSync();
    }
}
