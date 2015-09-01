package net.pmellaaho.rxapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import net.pmellaaho.rxapp.R;

public class MainActivity extends AppCompatActivity implements RepoInputFragment
        .OnRepoInputListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        RepoInputFragment firstFragment = new RepoInputFragment();
        manager.beginTransaction()
                .add(R.id.fragment_container, firstFragment).commit();
    }

    @Override
    public void onDataReady(String repo) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragment_container,
                ListFragment.newInstance(RepoInputFragment.OWNER, repo));

        if (manager.getBackStackEntryCount() == 0) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
