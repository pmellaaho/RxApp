package net.pmellaaho.rxapp.ui

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.google.android.material.composethemeadapter.MdcTheme
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import timber.log.Timber

@Composable
fun ContributorsListScreen(viewModel: ContributorsViewModel, backAction: () -> Unit) {
    val state: ViewState by viewModel.state.observeAsState(initial = Loading)

    MdcTheme {
        Scaffold(
            topBar = {
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
                            onClick = { viewModel.refreshData() }) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                )
            },
            content = { ContributorsListBody(state) }
        )
    }
}

@Composable
private fun ContributorsListBody(state: ViewState) {
    when (state) {
        is ShowList -> {
            ContributorsList(
                contributors = state.contributors
            )
        }
        is Loading -> ProgressIndicator()
        is EnterRepo -> {}

        is Error -> {
            Timber.e("request failed")
            ErrorMessage()
        }
    }
}
