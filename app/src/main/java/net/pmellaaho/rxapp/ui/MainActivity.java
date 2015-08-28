package net.pmellaaho.rxapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import net.pmellaaho.rxapp.model.Contributor;
import net.pmellaaho.rxapp.model.ContributorsModel;
import net.pmellaaho.rxapp.R;
import net.pmellaaho.rxapp.RxApp;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.google.common.base.Strings.isNullOrEmpty;

public class MainActivity extends AppCompatActivity {

    private static final String OWNER = "square";
    private static final String REPO = "retrofit";
    private static final String REPO_TO_SHOW = "repoToShow";

    private ProgressBar mProgress;
    private View mErrorText;
    private RecyclerView mList;

    private ContributorsAdapter mAdapter;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    ContributorsModel mModel;

    private Button mButton;
    private EditText mRepoEdit;
    private String mRepoToShow = "";

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

            if (!newRepo.equals(mRepoToShow) && !isNullOrEmpty(newRepo)) {
                mButton.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.startBtn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setEnabled(false);
                fetchData();
            }
        });

        mRepoEdit = (EditText) findViewById(R.id.repoEdit);

        mProgress = (ProgressBar) findViewById(R.id.progress);
        mErrorText = findViewById(R.id.errorText);
        mList = (RecyclerView) findViewById(R.id.recyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);
        mList.addItemDecoration(new DividerItemDecoration(this));

        mAdapter = new ContributorsAdapter();
        mList.setAdapter(mAdapter);
        mList.setItemAnimator(new DefaultItemAnimator());

        if (savedInstanceState == null) {
            mRepoEdit.setText(REPO);
        }
        mRepoEdit.addTextChangedListener(mRepoWatcher);

        mModel = ((RxApp)getApplication()).component().contributorsModel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(REPO_TO_SHOW, mRepoToShow);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRepoToShow = savedInstanceState.getString(REPO_TO_SHOW);

        boolean inputMatchesData = mRepoToShow.equals(mRepoEdit.getText().toString());

        if (inputMatchesData ) {
            mButton.setEnabled(false);
        }

        if (mModel.requestPending()) {
            Timber.d("resume with a new subscription");
            mProgress.setVisibility(View.VISIBLE);

            mSubscriptions.add(
                    mModel.getRequest()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ContributorsSubscriber()));


        } else if (mModel.hasCachedData() && inputMatchesData) {
            Timber.d("show cached data");
            fetchData();
        }
    }

    private void fetchData() {
        Timber.d("Fire request");
        mProgress.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mList.setVisibility(View.INVISIBLE);

        String newRepo = mRepoEdit.getText().toString();
        if (!mRepoToShow.equals(newRepo)) {
            // don't accept cached data
            mRepoToShow = newRepo;
            mModel.reset();
        }

        mSubscriptions.add(
                mModel.getContributors(OWNER, mRepoToShow)
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
            mProgress.setVisibility(View.INVISIBLE);
            mErrorText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        Timber.d("onPause(): unsubscribe");
        super.onPause();
        mSubscriptions.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
