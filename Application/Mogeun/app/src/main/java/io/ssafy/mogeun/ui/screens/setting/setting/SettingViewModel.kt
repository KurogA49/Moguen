package io.ssafy.mogeun.ui.screens.setting.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.ssafy.mogeun.MogeunApplication
import io.ssafy.mogeun.data.KeyRepository
import io.ssafy.mogeun.data.UserRepository
import io.ssafy.mogeun.model.DeleteUserResponse
import io.ssafy.mogeun.model.GetInbodyResponse
import kotlinx.coroutines.launch

class SettingViewModel(
    private val keyRepository: KeyRepository,
    private val UserRepository: UserRepository
) : ViewModel()  {
    fun deleteUserKey() {
        viewModelScope.launch {
            keyRepository.deleteKeyData()
        }
    }
    fun deleteUser() {
        lateinit var ret: DeleteUserResponse
        viewModelScope.launch {
            ret = UserRepository.deleteUser("", "")
            Log.d("getInbody", "$ret")
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MogeunApplication)
                val keyRepository = application.container.keyRepository
                val userRepository = application.container.userDataRepository
                SettingViewModel(keyRepository, UserRepository = userRepository)
            }
        }
    }
}