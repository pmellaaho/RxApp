package net.pmellaaho.rxapp.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import net.pmellaaho.rxapp.R
import net.pmellaaho.rxapp.RxApp
import net.pmellaaho.rxapp.databinding.FragmentRepoInputBinding
import net.pmellaaho.rxapp.model.Contributor
import net.pmellaaho.rxapp.model.ContributorsModel
import timber.log.Timber

class RepoInputFragment : Fragment() {
    private val disposables = CompositeDisposable()
    private var requestPending = false

    private var listener: OnRepoInputListener? = null
    private lateinit var model: ContributorsModel
    private lateinit var binding: FragmentRepoInputBinding

    interface OnRepoInputListener {
        fun onDataReady(repo: String)
    }

    inner class MyHandler {
        fun clicked(view: View) {
            binding.startBtn.isEnabled = false
            fetchData()
        }

        fun afterTextChanged(s: Editable) {
            val newRepo = s.toString()
            binding.startBtn.isEnabled = newRepo.isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("unsubscribe")
        disposables.dispose()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repo_input, container, false)
        binding.handler = MyHandler()

        if (savedInstanceState == null) {
            binding.repoEdit.setText(REPO)
        }

        model = (requireActivity().application as RxApp).component().contributorsModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.getBoolean(REQUEST_PENDING, false)) {
            if (model.request != null) {
                Timber.d("Subscribe to pending request")
                disposables.add(
                    model.request
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(ContributorsSubscriber())
                )
            }
        }
    }

    private fun fetchData() {
        Timber.d("Fetch data from RepoInputFragment")
        requestPending = true
        binding.progress.visibility = View.VISIBLE
        binding.errorText.visibility = View.INVISIBLE

        disposables.add(
            model.getContributors(OWNER, binding.repoEdit.text.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(ContributorsSubscriber())
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(REQUEST_PENDING, requestPending)
    }

    private inner class ContributorsSubscriber : DisposableObserver<List<Contributor?>?>() {

        override fun onNext(t: List<Contributor?>?) {
            Timber.d("received data from model")
        }

        override fun onComplete() {
            Timber.d("request completed")
            requestPending = false
            binding.progress.visibility = View.INVISIBLE
            listener?.onDataReady(binding.repoEdit.text.toString())
        }

        override fun onError(e: Throwable) {
            Timber.e(e, "request failed")
            binding.progress.visibility = View.INVISIBLE
            binding.errorText.visibility = View.VISIBLE
            binding.startBtn.isEnabled = true
            model.reset()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            activity as OnRepoInputListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement OnRepoInputListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val OWNER = "square"
        private const val REPO = "retrofit"
        private const val REQUEST_PENDING = "requestPending"
    }
}