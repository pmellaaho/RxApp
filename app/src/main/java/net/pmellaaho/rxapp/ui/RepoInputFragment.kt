package net.pmellaaho.rxapp.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
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

    inner class MyHandler {
        fun clicked() {
            binding.startBtn.isEnabled = false
            fetchData()
        }

        fun afterTextChanged(s: Editable) {
            val newRepo = s.toString()
            binding.startBtn.isEnabled = newRepo.isNotEmpty()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repo_input, container, false)
        binding.handler = MyHandler()

        if (savedInstanceState == null) {
            binding.repoEdit.editText?.setText(REPO)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        viewModel.state.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.errorText.visibility = View.INVISIBLE
                }

                is Error -> {
                    binding.progress.visibility = View.INVISIBLE
                    binding.errorText.visibility = View.VISIBLE
                    binding.startBtn.isEnabled = true
                }

                is Data -> {
                    Timber.d("request completed")
                    binding.progress.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.navigateToList.observe(viewLifecycleOwner) { event ->
            if (event.getContentIfNotHandled() == true) {
                findNavController().navigate(R.id.listFragment)
            }
        }
    }

    private fun fetchData() {
        Timber.d("Fetch data from RepoInputFragment")
        viewModel.fetchContributors(OWNER, binding.repoEdit.editText?.text.toString())
    }

}