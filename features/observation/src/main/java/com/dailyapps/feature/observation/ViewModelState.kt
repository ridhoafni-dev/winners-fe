package com.dailyapps.feature.observation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class ViewModelState<STATE, ACTION>(
    private val initialState: STATE,
): ViewModel() {

    private val _stateFlow = MutableStateFlow(initialState)
    val state: StateFlow<STATE> get() = _stateFlow

    abstract fun handleAction(action: ACTION)

    protected fun update(block: STATE.() -> STATE) {
        _stateFlow.value = block(_stateFlow.value)
    }

    fun currentState () = state.value

    fun restartState() {
        _stateFlow.value = initialState
    }

//    fun saveState() {
//        // stateKeeper.set(currentState() as Any)
//    }

}