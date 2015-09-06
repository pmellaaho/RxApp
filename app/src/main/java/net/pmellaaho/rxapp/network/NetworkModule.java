package net.pmellaaho.rxapp.network;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.pmellaaho.rxapp.R;
import net.pmellaaho.rxapp.RxApp;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import timber.log.Timber;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

@Module
public class NetworkModule {

    @Provides
    GitHubApi provideGitHubApi() {
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
