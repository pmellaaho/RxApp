package net.pmellaaho.rxapp.ui

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import net.pmellaaho.rxapp.TripleMediatorLiveData
import net.pmellaaho.rxapp.asLiveData
import net.pmellaaho.rxapp.model.Contributor
import net.pmellaaho.rxapp.model.ContributorsRepository
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val repository: ContributorsRepository
) : ViewModel() {

    private val _navigateEvent = MutableLiveData<Event<String>>()
    val navigateEvent = _navigateEvent.asLiveData()

    private val navigateBackEvent = MutableLiveData<Event<Any>>()
    private val fetchEvent = MutableLiveData<Event<Any>>()
    private val repo = MutableLiveData<String>()

    val state = TripleMediatorLiveData(navigateBackEvent, fetchEvent, repo).switchMap {
        fetchContributors(it)
    }

    private fun fetchContributors(triple: Triple<Event<Any>?, Event<Any>?, String?>) = liveData {
        val repoString = triple.third ?: return@liveData

        triple.first?.getContentIfNotHandled()?.let {
            _navigateEvent.value = Event(RxAppScreen.RepoInput.name)
            emit(EnterRepo)
            return@liveData
        }

        emit(Loading)
        try {
            val contributors = repository.getContributors(repoString)
            emit(ShowList(contributors))
            _navigateEvent.value = Event(RxAppScreen.ContributorsList.name)

        } catch (exception: Exception) {
            emit(Error)
        }
    }

    fun setRepo(repoName: String) {
        repo.value = repoName
    }

    fun refreshData() {
        fetchEvent.value = Event(Unit)
    }

    fun onBackPressed() {
        navigateBackEvent.value = Event(Unit)
    }

    sealed class ViewState {
        object Loading : ViewState()
        object Error : ViewState()
        object EnterRepo : ViewState()
        data class ShowList(val contributors: List<Contributor>) : ViewState()
    }
}