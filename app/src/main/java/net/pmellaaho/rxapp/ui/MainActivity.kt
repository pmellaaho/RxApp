package net.pmellaaho.rxapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

        if (navigateToList?.getContentIfNotHandled() == true) {
            navController.navigate(RxAppScreen.ContributorsList.name)
        }

        Scaffold(
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
                        ContributorsListScreen(viewModel = viewModel) { navController.popBackStack() }
                    }

                }
            }
        )
    }
}

enum class RxAppScreen {
    RepoInput,
    ContributorsList
}