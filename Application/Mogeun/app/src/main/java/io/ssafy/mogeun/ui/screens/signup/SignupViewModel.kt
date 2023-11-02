package io.ssafy.mogeun.ui.screens.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.ssafy.mogeun.MogeunApplication
import io.ssafy.mogeun.data.SignInRepository
import io.ssafy.mogeun.model.DupEmailResponse
import io.ssafy.mogeun.model.SignInResponse
import io.ssafy.mogeun.ui.screens.login.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val signInRepository: SignInRepository): ViewModel() {
    private val _dupEmailSuccess = MutableStateFlow(false)
    val dupEmailSuccess: StateFlow<Boolean> = _dupEmailSuccess.asStateFlow()
    fun dupEmail(email: String) {
        lateinit var ret: DupEmailResponse
        viewModelScope.launch {
            ret = signInRepository.dupEmail(email)
            Log.d("dupEmail", "$ret")

            if(ret.message == "SUCCESS") {
                _dupEmailSuccess.value = true
            }
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MogeunApplication)
                val signInRepository = application.container.userDataRepository
                SignupViewModel(signInRepository = signInRepository)
            }
        }
    }
}