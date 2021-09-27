package net.pmellaaho.rxapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.pmellaaho.rxapp.model.Contributor
import net.pmellaaho.rxapp.model.ContributorsRepository
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val repository: ContributorsRepository
) : ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state = _state as LiveData<ViewState>

    private val _navigateToList = MutableLiveData<Event<Boolean>>()
    val navigateToList = _navigateToList as LiveData<Event<Boolean>>

    fun fetchContributors(owner: String, repo: String) {
        _state.value = Loading
        viewModelScope.launch {
            try {
                val contributors: List<Contributor> = repository.getContributors(owner, repo)
                _state.value = Data(contributors)
                _navigateToList.value = Event(true)

            } catch (exception: Exception) {
                _state.value = Error
            }
        }
    }


    sealed class ViewState
    object Loading : ViewState()
    object Error : ViewState()
    data class Data(val contributors: List<Contributor>) : ViewState()

    /*
    fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>
     */
}