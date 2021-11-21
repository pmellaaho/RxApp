package net.pmellaaho.rxapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import timber.log.Timber

@Composable
fun ContributorsListScreen(viewModel: ContributorsViewModel) {
    val state: ViewState by viewModel.state.observeAsState(initial = Loading)
    ContributorsListBody(state)
}

@Composable
fun ContributorsListBody(state: ViewState) {
    when (state) {
        is ShowList -> {
            ContributorsList(
                contributors = state.contributors
            )
        }
        is Loading -> ProgressIndicator()
        is EnterRepo -> {
        }

        is Error -> {
            Timber.e("request failed")
            ErrorMessage()
        }
    }
}
