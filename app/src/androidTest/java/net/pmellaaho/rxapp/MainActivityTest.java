package net.pmellaaho.rxapp;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.pmellaaho.rxapp.model.Contributor;
import net.pmellaaho.rxapp.network.GitHubApi;
import net.pmellaaho.rxapp.network.NetworkModule;
import net.pmellaaho.rxapp.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.Path;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;
import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();

        final BehaviorDelegate<GitHubApi> delegate = mockRetrofit.create(GitHubApi.class);

        RxApp.NetworkComponent testComponent = DaggerRxApp_NetworkComponent.builder()
                .networkModule(new NetworkModule() {
                    @Override
                    public GitHubApi provideGitHubApi() {
                        return new FakeGitHubApi(delegate);
                    }
                })
                .build();

        app.setComponent(testComponent);
    }

    static final class FakeGitHubApi implements GitHubApi {
        private final BehaviorDelegate<GitHubApi> mDelegate;

        public FakeGitHubApi(BehaviorDelegate<GitHubApi> delegate) {
            mDelegate = delegate;
        }

        @Override
        public Observable<List<Contributor>> contributors(@Path("owner") String owner, @Path
                ("repo") String repo) {

            List<Contributor> tmpList = new ArrayList<>();
            tmpList.add(new Contributor("Jesse", 600));
            tmpList.add(new Contributor("Jake", 200));
            Observable<List<Contributor>> testObservable = Observable.just(tmpList);

            return mDelegate.returningResponse(testObservable).contributors(owner, repo);
        }
    }

    @Test
    public void listWithTwoContributors() {

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
}
