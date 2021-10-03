package net.pmellaaho.rxapp

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.appflate.restmock.RESTMockServer
import net.pmellaaho.rxapp.network.EndpointModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [EndpointModule::class]
)
class FakeEndpointModule {

    @Singleton
    @Provides
    fun provideEndPoint(): String = RESTMockServer.getUrl()

}