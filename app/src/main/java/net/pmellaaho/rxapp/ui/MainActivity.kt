package net.pmellaaho.rxapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ContributorsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RxApp(viewModel) }
    }
}

@Composable
fun RxApp(viewModel: ContributorsViewModel) {
    MdcTheme {
        val navController = rememberNavController()
        val navigateToList by viewModel.navigateToList.observeAsState()
        val backstackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = RxAppScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )

        if (navigateToList?.getContentIfNotHandled() == true) {
            navController.navigate(RxAppScreen.ContributorsList.name)
        }

        Scaffold(
            topBar = {
                RxAppBar(currentScreen = currentScreen,
                    backAction = { navController.popBackStack() },
                    onRefreshAction = { viewModel.refreshData() })
            },
            content = { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = RxAppScreen.RepoInput.name,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(RxAppScreen.RepoInput.name) {
                        RepoInputScreen(viewModel = viewModel)
                    }

                    composable(RxAppScreen.ContributorsList.name) {
                        ContributorsListScreen(viewModel = viewModel)
                    }

                }
            }
        )
    }
}

@Composable
fun RxAppBar(currentScreen: RxAppScreen, backAction: () -> Unit, onRefreshAction: () -> Unit) {
    when (currentScreen) {
        RxAppScreen.RepoInput -> TopAppBar(
            title = { Text(text = "Contributors") },
        )
        RxAppScreen.ContributorsList -> {
            TopAppBar(
                title = { Text(text = "Contributors") },
                navigationIcon = {
                    IconButton(
                        onClick = { backAction.invoke() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back arrow"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onRefreshAction.invoke() }) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    }
}

enum class RxAppScreen {
    RepoInput,
    ContributorsList;

    companion object {
        fun fromRoute(route: String?): RxAppScreen =
            when (route) {
                RepoInput.name -> RepoInput
                ContributorsList.name -> ContributorsList
                null -> RepoInput
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}