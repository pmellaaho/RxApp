package net.pmellaaho.rxapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.databinding.FragmentRepoInputBinding
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import timber.log.Timber

const val OWNER = "square"
private const val REPO = "retrofit"

@AndroidEntryPoint
class RepoInputFragment : Fragment() {
    private val viewModel: ContributorsViewModel by activityViewModels()
    private lateinit var binding: FragmentRepoInputBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repo_input, container, false)

        binding.composeView.apply {
            // Dispose the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                // entering the Compose world!
                RepoInputFragmentScreen(viewModel)
            }
        }

        return binding.root
    }

    @Composable
    private fun RepoInputFragmentScreen(viewModel: ContributorsViewModel) {
        val state: ContributorsViewModel.ViewState by viewModel.state.observeAsState(initial = EnterRepo)

        var text by rememberSaveable { mutableStateOf("") }
        val onTextChanged: (String) -> Unit = { text = it }

        MdcTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Contributors") },
                    )
                },
                content = {
                    when (state) {
                        is Loading -> ProgressIndicator()
                        is EnterRepo -> {
                            RepoInputFragmentContent(
                                modifier = Modifier.padding(12.dp),
                                text,
                                onTextChanged
                            )
                        }

                        is ShowList -> { /* NoOp */ }
                        is Error -> {
                            Timber.e("request failed")
                            ErrorMessage()
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun RepoInputFragmentContent(
        modifier: Modifier = Modifier,
        text: String,
        onTextChanged: (String) -> Unit
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())) {
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
                onClick = { fetchData(text) }) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        viewModel.navigateToList.observe(viewLifecycleOwner) { event ->
            if (event.getContentIfNotHandled() == true) {
                findNavController().navigate(R.id.listFragment)
            }
        }
    }

    private fun fetchData(repoName: String) {
        Timber.d("Fetch data from RepoInputFragment")
        viewModel.fetchContributors(OWNER, repoName)
    }

}