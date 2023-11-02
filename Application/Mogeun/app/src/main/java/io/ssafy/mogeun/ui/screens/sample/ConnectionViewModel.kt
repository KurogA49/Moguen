package io.ssafy.mogeun.ui.screens.sample

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.ssafy.mogeun.MogeunApplication
import io.ssafy.mogeun.data.Emg
import io.ssafy.mogeun.data.EmgRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class ConnectionViewModel(private val emgRepository: EmgRepository): ViewModel() {

    private val _emgInput = mutableStateOf<Emg>(Emg(0, 1, "left", 0.0, System.currentTimeMillis()))

    val emgInput = _emgInput

    val emgStream: StateFlow<Emg?> =
        emgRepository.getEmgStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = null
            )

    fun setDeviceId(id: Int) {
        _emgInput.value = _emgInput.value.copy(deviceId = id)
    }

    fun setSensingPart(part: String) {
        _emgInput.value = _emgInput.value.copy(sensingPart = part)
    }

    fun setValue(value: Double) {
        _emgInput.value = _emgInput.value.copy(value = value)
    }

    suspend fun saveData() {
        _emgInput.value = _emgInput.value.copy(time = System.currentTimeMillis())
        emgRepository.insertEmg(_emgInput.value)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}