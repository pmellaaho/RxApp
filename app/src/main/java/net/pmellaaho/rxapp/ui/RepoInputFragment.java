package net.pmellaaho.rxapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    ContributorsModel mModel;
    private boolean mRequestPending;

    public interface OnRepoInputListener {
        void onDataReady(String repo);
    }

    private TextWatcher mRepoWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String newRepo = s.toString();

            if (!isNullOrEmpty(newRepo)) {
                mButton.setEnabled(true);
            }
        }
    };
    
    public RepoInputFragment() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("unsubscribe");

        mSubscriptions.unsubscribe();

        RefWatcher refWatcher = RxApp.getRefWatcher();
        refWatcher.watch(this);
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repo_input, container, false);

        mButton = (Button) root.findViewById(R.id.startBtn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setEnabled(false);
                fetchData();
            }
        });

        mRepoEdit = (EditText) root.findViewById(R.id.repoEdit);
        mRepoEdit.addTextChangedListener(mRepoWatcher);
        if (savedInstanceState == null) {
            mRepoEdit.setText(REPO);
        }

        mProgress = (ProgressBar) root.findViewById(R.id.progress);
        mErrorText = root.findViewById(R.id.errorText);

        mModel = ((RxApp)getActivity().getApplication()).component().contributorsModel();
        return root;
    }
    
    private void fetchData() {
        Timber.d("Fetch data from RepoInputFragment");
        mRequestPending = true;
        mProgress.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);

        mSubscriptions.add(
                mModel.getContributors(OWNER, mRepoEdit.getText().toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ContributorsSubscriber()));
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
                mSubscriptions.add(
                        mModel.getRequest()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ContributorsSubscriber()));
            }
        }
    }

    private class ContributorsSubscriber extends Subscriber<List<Contributor>> {

        @Override
        public void onNext(List<Contributor> contributors) {
            Timber.d("received data from model");
        }

        @Override
        public void onCompleted() {
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
