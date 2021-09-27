package net.pmellaaho.rxapp.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.databinding.FragmentListBinding
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
        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity))
        binding.recyclerView.adapter = ContributorsAdapter()
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ContributorsViewModel.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                }

                is ContributorsViewModel.Error -> {
                    Timber.e("request failed")
                    binding.progress.visibility = View.INVISIBLE
                }

                is ContributorsViewModel.Data -> {
                    Timber.d("received data")
                    binding.progress.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    (binding.recyclerView.adapter as ContributorsAdapter).setData(viewState.contributors)
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