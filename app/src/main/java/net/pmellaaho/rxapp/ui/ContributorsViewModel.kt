package net.pmellaaho.rxapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.pmellaaho.rxapp.asLiveData
import net.pmellaaho.rxapp.model.Contributor
import net.pmellaaho.rxapp.model.ContributorsRepository
import net.pmellaaho.rxapp.ui.ContributorsViewModel.ViewState.*
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val repository: ContributorsRepository
) : ViewModel() {

    // Implement cache using LiveData which emits the last stored value when observer registers
    private val _state = MutableLiveData<ViewState>()
    val state = _state.asLiveData()

    private val _navigateEvent = MutableLiveData<Event<String>>()
    val navigateEvent = _navigateEvent.asLiveData()

    private lateinit var repo: String

    fun fetchContributors(repo: String) {
        _state.value = Loading
        this.repo = repo
        viewModelScope.launch {
            try {
                val contributors: List<Contributor> = repository.getContributors(repo)
                _state.value = ShowList(contributors)
                _navigateEvent.value = Event(RxAppScreen.ContributorsList.name)

            } catch (exception: Exception) {
                _state.value = Error
            }
        }
    }

    fun refreshData() {
        fetchContributors(repo)
    }

    fun onBackPressed() {
        if (_state.value is ShowList) {
            _state.value = EnterRepo
            _navigateEvent.value = Event(RxAppScreen.RepoInput.name)
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        object Error : ViewState()
        object EnterRepo : ViewState()
        data class ShowList(val contributors: List<Contributor>) : ViewState()
    }
}