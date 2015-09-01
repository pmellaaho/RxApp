package net.pmellaaho.rxapp.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.leakcanary.RefWatcher;

import net.pmellaaho.rxapp.R;
import net.pmellaaho.rxapp.RxApp;
import net.pmellaaho.rxapp.model.Contributor;
import net.pmellaaho.rxapp.model.ContributorsModel;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class ListFragment extends Fragment {
    public static final String ARG_OWNER = "owner";
    public static final String ARG_REPO = "repo";

    private RecyclerView mList;
    private ContributorsAdapter mAdapter;
    private ProgressBar mProgress;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    ContributorsModel mModel;
    
    private String mOwner;
    private String mRepo;
    
    public static ListFragment newInstance(String owner, String repo) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OWNER, owner);
        args.putString(ARG_REPO, repo);
        fragment.setArguments(args);
        return fragment;
    }
    
    public ListFragment() {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOwner = getArguments().getString(ARG_OWNER);
            mRepo = getArguments().getString(ARG_REPO);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        mProgress = (ProgressBar) root.findViewById(R.id.progress);

        mList = (RecyclerView) root.findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);
        mList.addItemDecoration(new DividerItemDecoration(getActivity()));

        mAdapter = new ContributorsAdapter();
        mList.setAdapter(mAdapter);
        mList.setItemAnimator(new DefaultItemAnimator());

        mModel = ((RxApp)getActivity().getApplication()).component().contributorsModel();
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("unsubscribe");

        mSubscriptions.unsubscribe();

        RefWatcher refWatcher = RxApp.getRefWatcher();
        refWatcher.watch(this);
    }

    private void fetchData() {
        Timber.d("Fetch data from ListFragment");
        mProgress.setVisibility(View.VISIBLE);
        mList.setVisibility(View.INVISIBLE);

        mSubscriptions.add(
                mModel.getContributors(mOwner, mRepo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ContributorsSubscriber()));
    }

    private class ContributorsSubscriber extends Subscriber<List<Contributor>> {

        @Override
        public void onNext(List<Contributor> contributors) {
            Timber.d("received data from model");
            mAdapter.setData(contributors);
        }

        @Override
        public void onCompleted() {
            Timber.d("request completed");
            mProgress.setVisibility(View.INVISIBLE);
            mList.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "request failed");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mModel.reset();
            fetchData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
