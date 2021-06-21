package net.pmellaaho.rxapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.ui.RepoInputFragment.OnRepoInputListener

class MainActivity : AppCompatActivity(), OnRepoInputListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            return
        }
        val manager = supportFragmentManager
        val firstFragment = RepoInputFragment()
        manager.beginTransaction()
            .add(R.id.fragment_container, firstFragment).commit()
    }

    override fun onDataReady(repo: String) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            ListFragment.newInstance(RepoInputFragment.OWNER, repo)
        )
        if (manager.backStackEntryCount == 0) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}