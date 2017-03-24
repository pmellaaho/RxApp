package net.pmellaaho.rxapp.model;

import net.pmellaaho.rxapp.network.GitHubApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;

@Singleton
public class ContributorsModel {

    private final GitHubApi mApi;

    // Implement cache using An AsyncSubject which emits the last value
    // (and only the last value) emitted by the source Observable,
    // and only after that source Observable completes.
    private AsyncSubject<List<Contributor>> mAsyncSubject;

    private String mOwner;
    private String mRepo;

    @Inject
    public ContributorsModel(GitHubApi api) {
        mApi = api;
    }

    public void reset() {
        mAsyncSubject = null;
        mOwner = null;
        mRepo = null;
    }

    public Observable<List<Contributor>> getRequest() {
        return mAsyncSubject;
    }

    public Observable<List<Contributor>> getContributors(String owner, String repo) {

        if (mOwner == null || mRepo == null) {
            mOwner = owner;
            mRepo = repo;

        } else if (!mOwner.equals(owner) || !mRepo.equals(repo)) {
            // can't use cached data
            mAsyncSubject = null;
            mOwner = owner;
            mRepo = repo;
        }

        if (mAsyncSubject == null) {
            mAsyncSubject = AsyncSubject.create();

            mApi.contributors(owner, repo)
                    .subscribeOn(Schedulers.io())
                    .subscribe(mAsyncSubject);
        }
        return mAsyncSubject;
    }
}