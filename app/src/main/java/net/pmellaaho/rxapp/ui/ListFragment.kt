package net.pmellaaho.rxapp.ui

import android.os.Bundle
import android.view.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.databinding.FragmentListBinding
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import timber.log.Timber

const val ARG_OWNER = "owner"
const val ARG_REPO = "repo"


@AndroidEntryPoint
class ListFragment : Fragment() {
    private lateinit var owner: String
    private lateinit var repo: String

    private lateinit var binding: FragmentListBinding
    private val viewModel: ContributorsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            owner = requireArguments().getString(ARG_OWNER)!!
            repo = requireArguments().getString(ARG_REPO)!!
        }
    }

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
                ListFragmentScreen(viewModel)
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.composeView.visibility = View.INVISIBLE
                }

                is Error -> {
                    Timber.e("request failed")
                    binding.progress.visibility = View.INVISIBLE
                }

                is Data -> {
                    Timber.d("received data")
                    binding.progress.visibility = View.INVISIBLE
                    binding.composeView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            Timber.d("Fetch data from ListFragment")
            viewModel.fetchContributors(owner, repo)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

@Composable
private fun ListFragmentScreen(viewModel: ContributorsViewModel) {
    val state: ViewState by viewModel.state.observeAsState(initial = Loading)

    if (state is Data) {
        ContributorsList(
            contributors = (state as Data).contributors
        )
    }
}