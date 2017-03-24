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
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

@Module
public class NetworkModule {

    @Provides
    GitHubApi provideGitHubApi() {
        final String GITHUB_ENDPOINT = "https://api.github.com/";
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        final String githubToken = RxApp.get().getResources().getString(R.string
                .github_oauth_token);

        if (!isNullOrEmpty(githubToken)) {
            builder.addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request authorizedRequest = originalRequest.newBuilder()
                            .header("Authorization", format("token %s", githubToken))
                            .build();

                    return chain.proceed(authorizedRequest);
                }
            });
        }

        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Timber.d("Sending request: " + request.url() + " with headers: " + request.headers());
                return chain.proceed(request);
            }
        });

        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_ENDPOINT)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(GitHubApi.class);
    }
}
