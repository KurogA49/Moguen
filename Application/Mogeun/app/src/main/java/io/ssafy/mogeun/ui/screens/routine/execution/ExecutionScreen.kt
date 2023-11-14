package io.ssafy.mogeun.ui.screens.routine.execution

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.ssafy.mogeun.R
import io.ssafy.mogeun.ui.BluetoothViewModel
import io.ssafy.mogeun.ui.components.AlertDialogCustom
import io.ssafy.mogeun.ui.components.ElevatedGif
import io.ssafy.mogeun.ui.screens.routine.execution.components.ExerciseProgress
import io.ssafy.mogeun.ui.screens.routine.execution.components.RoutineProgress
import io.ssafy.mogeun.ui.screens.routine.execution.components.SensorBottomSheet
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExecutionScreen(viewModel: BluetoothViewModel, routineKey: Int, navController: NavHostController) {
    val emgState by viewModel.emgState.collectAsState()
    val btState by viewModel.btState.collectAsState()
    val routineState by viewModel.routineState.collectAsState()
    val elapsedTime by viewModel.elaspedTime.collectAsState()

    var openEndDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val ret = async {
            viewModel.getUserKey()
        }.await()
        if (!routineState.onProcess) {
            viewModel.startRoutine(routineKey)
        }
    }

    LaunchedEffect(routineKey) {
        viewModel.getPlanList(routineKey)
    }

    if(routineState.planList == null) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                trackColor = MaterialTheme.colorScheme.secondary,
            )
        }
    } else {
        val routineSize = routineState.planList!!.data.size
        val pagerState = rememberPagerState { routineSize }

        LaunchedEffect(pagerState.currentPage) {
            if(!routineState.planDetailsRequested) viewModel.getSetOfRoutine()
        }

        if(routineState.planDetails.isNotEmpty()) {

            LaunchedEffect(Unit) {
                if(elapsedTime.minute == 0 && elapsedTime.second == 0) {
                    coroutineScope.launch {
                        viewModel.runTimer()
                    }
                }
            }

            Column {
                HorizontalPager(
                    pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val plan = routineState.planList!!.data[page]
                    val imgPath = plan.imagePath
                    Column {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(12.dp)
                        ) {
                            ElevatedGif(imgPath = imgPath,
                                Modifier
                                    .width(300.dp)
                                    .height(200.dp))
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            Text("${plan.name}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            FilledTonalIconButton(
                                onClick = viewModel::showBottomSheet,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(44.dp)
                            ){
                                Icon(painter = painterResource(id = R.drawable.heart_rate), null, modifier = Modifier.fillMaxSize(0.8f))
                            }
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            ExerciseProgress(
                                emgState,
                                routineState.planDetails[page].setOfRoutineDetail,
                                {viewModel.addSet(plan.planKey)},
                                {idx -> viewModel.removeSet(plan.planKey, idx)},
                                {idx, weight -> viewModel.setWeight(plan.planKey, idx, weight)},
                                {idx, rep -> viewModel.setRep(plan.planKey, idx, rep)}
                            )
                        }
                    }
                }
                RoutineProgress(pagerState.currentPage + 1, routineSize, elapsedTime) {
                    openEndDialog = true
                }

                SensorBottomSheet(state = routineState.showBottomSheet, hide = viewModel::hideBottomSheet, navToConnection = {navController.navigate("Connection")}, btState = btState, sensingPart = routineState.planList!!.data[pagerState.currentPage].mainPart.imagePath)
            }
        }
    }
    when {
        openEndDialog -> {
            AlertDialogCustom(
                onDismissRequest = {
                    openEndDialog = false
                                   },
                onConfirmation = {
                    openEndDialog = false
                    viewModel.endRoutine()
                    navController.navigate("RecordDetail/${routineState.reportKey}")
                                 },
                dialogTitle = "오늘의 루틴 종료",
                dialogText = "현재까지의 진행상황을 기록하고 운동을 종료합니다.",
                icon = Icons.Default.PauseCircle
            )
        }
    }
}