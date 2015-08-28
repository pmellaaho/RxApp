package net.pmellaaho.rxapp;

import net.pmellaaho.rxapp.model.ContributorsModel;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MockNetworkModule {

    @Provides
    @Singleton
    ContributorsModel provideContributorsModel() {
        return Mockito.mock(ContributorsModel.class);
    }
}
