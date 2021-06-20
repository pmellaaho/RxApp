package net.pmellaaho.rxapp;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import net.pmellaaho.rxapp.model.Contributor;
import net.pmellaaho.rxapp.model.ContributorsModel;
import net.pmellaaho.rxapp.network.NetworkComponent;
import net.pmellaaho.rxapp.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.Observable;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyString;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    ContributorsModel mModel;

    @Singleton
    @Component(modules = MockNetworkModule.class)
    public interface MockNetworkComponent extends NetworkComponent {
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity.

    @Before
    public void setUp() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        RxApp app = (RxApp) instrumentation.getTargetContext()
                .getApplicationContext();

        MockNetworkComponent testComponent = DaggerMainActivityTest_MockNetworkComponent.builder()
                .mockNetworkModule(new MockNetworkModule())
                .build();
        app.setComponent(testComponent);
        mModel = testComponent.contributorsModel();
    }

    @Test
    public void listWithTwoContributors() {

        // GIVEN
        List<Contributor> tmpList = new ArrayList<>();
        tmpList.add(new Contributor("Jesse", 600));
        tmpList.add(new Contributor("Jake", 200));

        Observable<List<Contributor>> testObservable = Observable.just(tmpList);

        Mockito.when(mModel.getContributors(anyString(), anyString()))
                .thenReturn(testObservable);

        // WHEN
        mActivityRule.launchActivity(new Intent());
        onView(withId(R.id.startBtn)).perform(click());

        // THEN
        onView(ViewMatchers.nthChildOf(withId(R.id.recyclerView), 0))
                .check(matches(hasDescendant(withText("Jesse"))));

        onView(ViewMatchers.nthChildOf(withId(R.id.recyclerView), 0))
                .check(matches(hasDescendant(withText("600"))));

        onView(ViewMatchers.nthChildOf(withId(R.id.recyclerView), 1))
                .check(matches(hasDescendant(withText("Jake"))));

        onView(ViewMatchers.nthChildOf(withId(R.id.recyclerView), 1))
                .check(matches(hasDescendant(withText("200"))));
    }

    @Test
    public void errorFromNetwork() {

        // GIVEN
        // create an Observable that emits nothing and then signals an error
        Observable<List<Contributor>> errorEmittingObservable =
                Observable.error(new IllegalArgumentException());

        Mockito.when(mModel.getContributors(anyString(), anyString()))
                .thenReturn(errorEmittingObservable);

        // WHEN
        mActivityRule.launchActivity(new Intent());
        onView(withId(R.id.startBtn)).perform(click());

        // THEN
        onView(withId(R.id.errorText))
                .check(matches(isDisplayed()));
    }
}
