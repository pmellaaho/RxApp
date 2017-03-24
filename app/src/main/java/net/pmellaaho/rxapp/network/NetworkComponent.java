package net.pmellaaho.rxapp.network;


import net.pmellaaho.rxapp.model.ContributorsModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = NetworkModule.class)
public interface NetworkComponent {
    ContributorsModel contributorsModel();
}
