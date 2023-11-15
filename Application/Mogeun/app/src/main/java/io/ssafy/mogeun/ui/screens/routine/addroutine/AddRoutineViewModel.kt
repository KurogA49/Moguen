package io.ssafy.mogeun.ui.screens.routine.addroutine

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.ssafy.mogeun.MogeunApplication
import io.ssafy.mogeun.data.KeyRepository
import io.ssafy.mogeun.data.RoutineRepository
import io.ssafy.mogeun.model.AddRoutineResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import io.ssafy.mogeun.model.AddAllExerciseResponse
import io.ssafy.mogeun.model.ListAllExerciseResponsedata
import io.ssafy.mogeun.model.ListMyExerciseResponse
import io.ssafy.mogeun.model.ListMyExerciseResponseData
import io.ssafy.mogeun.model.ListMyExerciseResponseDataExercises
import io.ssafy.mogeun.model.MyExerciseResponse
import io.ssafy.mogeun.model.MyExerciseResponseData

class AddRoutineViewModel(
    private val routineRepository: RoutineRepository,
    private val keyRepository: KeyRepository
) : ViewModel() {
    var userKey by mutableStateOf<Int?>(null)
    private val _listMyExerciseSuccess = MutableStateFlow(false)
    var exerciseList: List<ListMyExerciseResponseData> = mutableStateListOf()
    private val _myExerciseSuccess = MutableStateFlow(false)
    var exerciseExplain by mutableStateOf<MyExerciseResponseData?>(null)

    fun updateUserKey(value: Int?) {
        userKey = value
    }
    fun getUserKey() {
        viewModelScope.launch {
            val key = keyRepository.getKey().first()
            val userKey = key?.userKey
            Log.d("getUserKey", "사용자 키: $userKey")
            updateUserKey(userKey)
        }
    }
    fun listMyExercise(routineKey: Int?){
        lateinit var ret: ListMyExerciseResponse
        viewModelScope.launch{
            ret = routineRepository.listMyExercise(routineKey)
            Log.d("listMyexercise", "$ret")
            if (ret.message == "SUCCESS"){
                _listMyExerciseSuccess.value = true
                exerciseList = ret.data
            }
        }
    }
    fun myExercise(execKey: Int?){
        lateinit var ret: MyExerciseResponse
        viewModelScope.launch{
            ret = routineRepository.myExercise(execKey)
            Log.d("myExercise", "$ret")
            if (ret.message == "SUCCESS"){
                _myExerciseSuccess.value = true
                exerciseExplain = ret.data
            }
            Log.d("next", "${ret.data.name}")
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MogeunApplication)
                val addRoutineRepository = application.container.routineRepository
                val keyRepository = application.container.keyRepository
                AddRoutineViewModel(routineRepository = addRoutineRepository, keyRepository)
            }
        }
    }
}