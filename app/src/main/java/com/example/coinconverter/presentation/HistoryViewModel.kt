package com.example.coinconverter.presentation

import androidx.lifecycle.*
import com.example.coinconverter.data.domain.ListExchangeUseCase
import com.example.coinconverter.data.model.ExchangeResponseValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val listExchangeViewModel : ListExchangeUseCase
): ViewModel(),LifecycleObserver {


    private val _state = MutableLiveData<State>()
    val state = _state

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun getExchanges(){
        viewModelScope.launch {
            listExchangeViewModel()
                .flowOn(Dispatchers.Main)
                .onStart { _state.value = State.Loading }
                .catch { _state.value = State.Error(it) }
                .collect { _state.value = State.Success(it) }
        }
    }
    sealed class State{
        object Loading: State()

        data class Success(val list: List<ExchangeResponseValue>):State()
        data class Error(val error: Throwable): State()
    }
}

