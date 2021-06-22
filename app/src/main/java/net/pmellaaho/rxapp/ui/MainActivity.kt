package net.pmellaaho.rxapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.ui.RepoInputFragment.OnRepoInputListener

class MainActivity : AppCompatActivity(), OnRepoInputListener {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
    }

    override fun onDataReady(repo: String) {
        navController.navigate(
            R.id.listFragment, bundleOf(
                ARG_OWNER to RepoInputFragment.OWNER,
                ARG_REPO to repo
            )
        )
    }
}