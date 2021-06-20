package net.pmellaaho.rxapp.network;


import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides
    GitHubApi provideGitHubApi() {
        final String GITHUB_ENDPOINT = "https://api.github.com/";

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        /* Use OAuth token to get more than 60 requests/hour
        final String githubToken = RxApp.get().getResources().getString(R.string
                .github_oauth_token);

        if (!isNullOrEmpty(githubToken)) {
            builder.addNetworkInterceptor(chain -> {
                Request originalRequest = chain.request();
                Request authorizedRequest = originalRequest.newBuilder()
                        .header("Authorization", format("token %s", githubToken))
                        .build();

                return chain.proceed(authorizedRequest);
            });
        }
         */

        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_ENDPOINT)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(GitHubApi.class);
    }
}
