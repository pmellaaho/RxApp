package net.pmellaaho.rxapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class PairMediatorLiveData<F, S>(firstLiveData: LiveData<F>, secondLiveData: LiveData<S>) :
    MediatorLiveData<Pair<F?, S?>>() {
    init {
        addSource(firstLiveData) { firstLiveDataValue: F ->
            value = firstLiveDataValue to secondLiveData.value
        }
        addSource(secondLiveData) { secondLiveDataValue: S ->
            value = firstLiveData.value to secondLiveDataValue
        }
    }
}

class TripleMediatorLiveData<F, S, T>(
    firstLiveData: LiveData<F>,
    secondLiveData: LiveData<S>,
    thirdLiveData: LiveData<T>
) : MediatorLiveData<Triple<F?, S?, T?>>() {
    init {
        addSource(firstLiveData) { firstLiveDataValue: F ->
            value = Triple(firstLiveDataValue, secondLiveData.value, thirdLiveData.value)
        }
        addSource(secondLiveData) { secondLiveDataValue: S ->
            value = Triple(firstLiveData.value, secondLiveDataValue, thirdLiveData.value)
        }
        addSource(thirdLiveData) { thirdLiveDataValue: T ->
            value = Triple(firstLiveData.value, secondLiveData.value, thirdLiveDataValue)
        }
    }
}