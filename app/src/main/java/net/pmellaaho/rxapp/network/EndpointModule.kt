package net.pmellaaho.rxapp.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EndpointModule {

    private val GITHUB_ENDPOINT = "https://api.github.com/"

    @Singleton
    @Provides
    fun provideEndPoint(): String = GITHUB_ENDPOINT


}