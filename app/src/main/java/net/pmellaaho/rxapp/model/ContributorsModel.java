package net.pmellaaho.rxapp.model;

import net.pmellaaho.rxapp.network.GitHubApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.AsyncSubject;

@Singleton
public class ContributorsModel {

    private final GitHubApi mApi;

    // Implement cache using An AsyncSubject which emits the last value
    // (and only the last value) emitted by the source Observable,
    // and only after that source Observable completes.
    private AsyncSubject<List<Contributor>> mAsyncSubject;

    @Inject
    public ContributorsModel(GitHubApi api) {
        mApi = api;
    }

    public void reset() {
        mAsyncSubject = null;
    }

    public Observable<List<Contributor>> getRequest() {
        return mAsyncSubject;
    }

    public Observable<List<Contributor>> getContributors(String owner, String repo) {

        if (mAsyncSubject == null) {
            mAsyncSubject = AsyncSubject.create();

            mApi.contributors(owner, repo)
                    .subscribe(mAsyncSubject);
        }
        return mAsyncSubject;
    }

    public boolean hasCachedData() {
        return mAsyncSubject != null;
    }

    public boolean requestPending() {
        return mAsyncSubject != null && !mAsyncSubject.hasCompleted();
    }

}