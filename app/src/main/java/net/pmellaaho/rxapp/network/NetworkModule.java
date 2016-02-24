package net.pmellaaho.rxapp.network;


import net.pmellaaho.rxapp.R;
import net.pmellaaho.rxapp.RxApp;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import timber.log.Timber;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

@Module
public class NetworkModule {

    @Provides
    public GitHubApi provideGitHubApi() {
        final String GITHUB_ENDPOINT = "https://api.github.com/";
        OkHttpClient okHttpClient = new OkHttpClient();

        final String githubToken = RxApp.get().getResources().getString(R.string
                .github_oauth_token);

        if (!isNullOrEmpty(githubToken)) {

            okHttpClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request authorizedRequest = originalRequest.newBuilder()
                            .header("Authorization", format("token %s", githubToken))
                            .build();

                    return chain.proceed(authorizedRequest);
                }
            });

            okHttpClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Timber.d("Sending request: " + request.url() + " with headers: " + request.headers());
                    return chain.proceed(request);
                }
            });
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_ENDPOINT)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(GitHubApi.class);
    }
}
