package net.pmellaaho.rxapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.databinding.FragmentListBinding
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import timber.log.Timber

@AndroidEntryPoint
class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ContributorsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)

        binding.composeView.apply {
            // Dispose the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                // entering the Compose world!
                ListFragmentScreen(viewModel) { findNavController().popBackStack() }
            }
        }
        return binding.root
    }
}

@Composable
private fun ListFragmentScreen(viewModel: ContributorsViewModel, backAction: () -> Unit) {
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
            content = { ListFragmentContent(state) }
        )
    }
}

@Composable
private fun ListFragmentContent(state: ViewState) {
    when (state) {
        is Data -> {
            ContributorsList(
                contributors = state.contributors
            )
        }
        is Loading -> ProgressIndicator()

        is Error -> {
            Timber.e("request failed")
            ErrorMessage()
        }
    }
}