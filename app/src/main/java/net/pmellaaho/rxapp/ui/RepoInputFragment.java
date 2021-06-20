package net.pmellaaho.rxapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import net.pmellaaho.rxapp.R;
import net.pmellaaho.rxapp.RxApp;
import net.pmellaaho.rxapp.databinding.FragmentRepoInputBinding;
import net.pmellaaho.rxapp.model.Contributor;
import net.pmellaaho.rxapp.model.ContributorsModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RepoInputFragment extends Fragment {
    public static final String OWNER = "square";
    private static final String REPO = "retrofit";
    private static final String REQUEST_PENDING = "requestPending";

    private OnRepoInputListener mListener;

    private Button mButton;
    private EditText mRepoEdit;
    private ProgressBar mProgress;
    private View mErrorText;

    private CompositeDisposable mDisposables = new CompositeDisposable();

    ContributorsModel mModel;
    private boolean mRequestPending;

    public interface OnRepoInputListener {
        void onDataReady(String repo);
    }

    public class MyHandler {
        public void clicked(View v) {
            mButton.setEnabled(false);
            fetchData();
        }

        public void afterTextChanged(Editable s) {
            String newRepo = s.toString();

            if (!isNullOrEmpty(newRepo)) {
                mButton.setEnabled(true);
            } else {
                mButton.setEnabled(false);
            }
        }
    }

    public RepoInputFragment() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("unsubscribe");
        mDisposables.dispose();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentRepoInputBinding binding = DataBindingUtil.inflate(inflater, R.layout
                .fragment_repo_input, container, false);

        binding.setHandler(new MyHandler());

        mButton = binding.startBtn;
        mRepoEdit = binding.repoEdit;
        if (savedInstanceState == null) {
            mRepoEdit.setText(REPO);
        }

        mProgress = binding.progress;
        mErrorText = binding.errorText;

        mModel = ((RxApp) getActivity().getApplication()).component().contributorsModel();
        return binding.getRoot();
    }

    private void fetchData() {
        Timber.d("Fetch data from RepoInputFragment");
        mRequestPending = true;
        mProgress.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);

        mDisposables.add(
                mModel.getContributors(OWNER, mRepoEdit.getText().toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new ContributorsSubscriber()));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(REQUEST_PENDING, mRequestPending);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean(REQUEST_PENDING, false)) {

            if (mModel.getRequest() != null) {
                Timber.d("Subscribe to pending request");
                mDisposables.add(
                        mModel.getRequest()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new ContributorsSubscriber()));
            }
        }
    }

    private class ContributorsSubscriber extends DisposableObserver<List<Contributor>> {

        @Override
        public void onNext(List<Contributor> contributors) {
            Timber.d("received data from model");
        }

        @Override
        public void onComplete() {
            Timber.d("request completed");
            mRequestPending = false;
            mProgress.setVisibility(View.INVISIBLE);

            if (mListener != null) {
                mListener.onDataReady(mRepoEdit.getText().toString());
            }
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "request failed");
            mProgress.setVisibility(View.INVISIBLE);
            mErrorText.setVisibility(View.VISIBLE);
            mButton.setEnabled(true);
            mModel.reset();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRepoInputListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRepoInputListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
