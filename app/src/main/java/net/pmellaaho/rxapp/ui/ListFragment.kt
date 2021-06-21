package net.pmellaaho.rxapp.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.RxApp
import net.pmellaaho.rxapp.databinding.FragmentListBinding
import net.pmellaaho.rxapp.model.Contributor
import net.pmellaaho.rxapp.model.ContributorsModel
import timber.log.Timber

class ListFragment : Fragment() {
    private val disposables = CompositeDisposable()
    private var owner: String? = null
    private var repo: String? = null

    private lateinit var model: ContributorsModel
    private lateinit var binding: FragmentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            owner = requireArguments().getString(ARG_OWNER)
            repo = requireArguments().getString(ARG_REPO)
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
        model = (requireActivity().application as RxApp).component().contributorsModel()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("unsubscribe")
        disposables.dispose()
    }

    private fun fetchData() {
        Timber.d("Fetch data from ListFragment")
        binding.progress.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.INVISIBLE
        disposables.add(
            model.getContributors(owner, repo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(ContributorsObserver())
        )
    }

    private inner class ContributorsObserver : DisposableObserver<List<Contributor?>?>() {
        override fun onNext(contributors: List<Contributor?>?) {
            Timber.d("received data from model")
            (binding.recyclerView.adapter as ContributorsAdapter).setData(contributors)
        }

        override fun onComplete() {
            Timber.d("request completed")
            binding.progress.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        }

        override fun onError(e: Throwable) {
            Timber.e(e, "request failed")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            model.reset()
            fetchData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ARG_OWNER = "owner"
        const val ARG_REPO = "repo"

        fun newInstance(owner: String?, repo: String?) =
            ListFragment().apply {
                arguments = bundleOf(
                    ARG_OWNER to owner,
                    ARG_REPO to repo
                )
            }
    }
}