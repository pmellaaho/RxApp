package net.pmellaaho.rxapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import timber.log.Timber

const val OWNER = "square"

@Composable
fun RepoInputScreen(viewModel: ContributorsViewModel) {
    val state: ContributorsViewModel.ViewState by viewModel.state.observeAsState(initial = EnterRepo)
    var text by rememberSaveable { mutableStateOf("") }
    val onTextChanged: (String) -> Unit = { text = it }
    val onClickListener: (String) -> Unit = { viewModel.fetchContributors(OWNER, it) }

    when (state) {
        is Loading -> ProgressIndicator()
        is EnterRepo -> {
            RepoInputBody(
                modifier = Modifier.padding(12.dp),
                text,
                onTextChanged,
                onClickListener
            )
        }

        is ShowList -> { /* NoOp */
        }
        is Error -> {
            Timber.e("request failed")
            ErrorMessage()
        }
    }
}

@Composable
private fun RepoInputBody(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    onClickListener: (String) -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            style = MaterialTheme.typography.h5,
            text = "Show the contributors to open source projects by Square"
        )

        val extraPadding = Modifier.padding(32.dp)
        OutLinedTextField(
            modifier = extraPadding.align(Alignment.CenterHorizontally),
            text = text,
            onTextChanged = onTextChanged
        )

        Button(
            modifier = extraPadding.align(Alignment.End),
            enabled = text.isNotEmpty(), // todo: disables state looks bad
            onClick = { onClickListener(text) }) {
            Text(
                text = "Start"
            )
        }
    }
}

@Composable
fun OutLinedTextField(
    modifier: Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = text,
        label = { Text(text = "repository name") },
        onValueChange = onTextChanged
    )
}