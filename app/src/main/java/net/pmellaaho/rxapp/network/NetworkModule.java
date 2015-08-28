package net.pmellaaho.rxapp.network;

import net.pmellaaho.rxapp.R;
import net.pmellaaho.rxapp.RxApp;
import net.pmellaaho.rxapp.network.GitHubApi;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

@Module
public class NetworkModule {

    @Provides
    GitHubApi provideGitHubApi() {
        final String GITHUB_ENDPOINT = "https://api.github.com/";

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setEndpoint(GITHUB_ENDPOINT);

        final String githubToken = RxApp.get().getResources().getString(R.string
                .github_oauth_token);
        if (!isNullOrEmpty(githubToken)) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", format("token %s", githubToken));
                }
            });
        }

        return builder.build().create(GitHubApi.class);
    }
}
