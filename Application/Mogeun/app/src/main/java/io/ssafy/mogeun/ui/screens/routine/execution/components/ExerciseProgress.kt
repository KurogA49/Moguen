@file:Suppress("IMPLICIT_CAST_TO_ANY")

package io.ssafy.mogeun.ui.screens.routine.execution.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ssafy.mogeun.R
import io.ssafy.mogeun.model.SetOfRoutineDetail
import io.ssafy.mogeun.ui.screens.routine.execution.EmgUiState
import io.ssafy.mogeun.ui.screens.routine.execution.SetOfPlan
import io.ssafy.mogeun.ui.screens.routine.execution.SetProgress
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.jtransforms.fft.DoubleFFT_1D

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ExerciseProgress(
    emgUiState: EmgUiState,
    planInfo: List<SetProgress>,
    addSet: () -> Unit,
    removeSet: (Int) -> Unit,
    setWeight: (Int, Int) -> Unit,
    setRep: (Int, Int) -> Unit,
    startSet: (Int) -> Unit,
    addCnt: (Int) -> Unit,
    endSet: (Int) -> Unit,
    inProgress: Boolean,
    muscleAverage: Double
){
    val totalSet = planInfo.size
    val setCntList = (1..totalSet).map { it }

    var selectedTab by remember { mutableIntStateOf(0) }

    val setProgress = planInfo[selectedTab]


    //시작 종료
    var isStarting by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth(0.95f)
            .background(color = Color(0xFFF7F7F7))
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Box(modifier = Modifier //---------header---------
            .fillMaxHeight(0.15f)
            .background(color = Color(0xFFDFEAFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopStart
            ){
                ScrollableTabRow(setCntList.map { "$it 세트" }, selectedTab, { index -> selectedTab = index }, inProgress)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp, vertical = 0.dp),
                contentAlignment = Alignment.CenterEnd
            ){
                Button(onClick = {
                    addSet()
                },
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                        .padding(0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(0.dp),
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "세트 추가",
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier //---------------body-------------------
            .fillMaxHeight(0.7f)
            .fillMaxWidth()
        ) {
            Box(modifier = Modifier //무계, 횟수
                .fillMaxHeight()
                .fillMaxWidth(0.35f)
                .background(Color(0xFFF7F7F7)),
                contentAlignment = Alignment.Center
            ){
                DateSelectionSection(
                    onWeightChosen = { setWeight(selectedTab + 1, it.toInt()) },
                    onRepChosen = {setRep(selectedTab + 1, it.toInt())},
                    preWeight = planInfo[selectedTab].targetWeight,
                    preRep = planInfo[selectedTab].targetRep,
                    inProgress = inProgress
                )
            }
            Box(
                modifier = Modifier//EMG 신호 표기
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(Color(0xFFF7F7F7)),
            ){
                EMGCollector(emgUiState, isStarting, setProgress, {addCnt(selectedTab + 1)}, inProgress, muscleAverage)
            }
        }
        Box(
            modifier = Modifier //---------footer---------
                .fillMaxSize()
                .background(Color(0xFFF7F7F7)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(12.dp)),
                horizontalArrangement = Arrangement.Center,
            )
            {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(0.3f)
//                        .fillMaxHeight()
//                        .background(color = Color.White)
//                        .clickable {
//                            if (selectedTab == totalSet - 1) selectedTab = totalSet - 2
//                            removeSet(selectedTab + 1)
//                        },
//                ){
//                    Text(
//                        text = "세트 삭제",
//                        modifier = Modifier.align(Alignment.Center),
//                    )
//                }
                TextButton(
                    enabled = !inProgress,
                    onClick = {
                        if (selectedTab == totalSet - 1) selectedTab = totalSet - 2
                        removeSet(selectedTab + 1)
                              },
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight()
                        .background(color = Color.White)

                ) {
                    Text(text = "세트 삭제")
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.End

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clickable {
                                    startSet(selectedTab + 1)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ){
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = null,
                                tint = Color(0xFF556FF7),
                                modifier = Modifier.size(20.dp),
                            )
                            Text(text = "시작",fontSize = 15.sp, textAlign = TextAlign.Center)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 8.dp)
                                .clickable {
                                    endSet(selectedTab + 1)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ){
                            Icon(
                                painter = painterResource(id = R.drawable.removecirclestop),
                                contentDescription = "contentDescription",
                                tint = Color(0xFFFFD5D5),
                                modifier = Modifier
                                    .size(21.dp)
                                    .padding(2.dp)
                            )
                            Text(text = "종료")
                        }
                    }
                }
            }
        }
    }
}

@Composable//header
private fun ScrollableTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    inProgress: Boolean,
) {
    androidx.compose.material3.ScrollableTabRow(
        containerColor = Color(0xFFDFEAFF),
        selectedTabIndex = selectedTab,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        edgePadding = 0.dp,
    ) {
        tabs.forEachIndexed { index, text ->
            Tab(
                enabled = !inProgress,
                selected = selectedTab == index,
                onClick = {
                    onTabClick(index)
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color.Black,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(20.dp, 36.dp)
            ) {
                Text(text = text, fontSize = 14.sp)
            }
        }
    }
}

//private fun ScrollButton(){}

//-------------스크롤 버튼-------------------

@Composable
fun DateSelectionSection(
    onWeightChosen: (String) -> Unit,
    onRepChosen: (String) -> Unit,
    preWeight: Int,
    preRep: Int,
    inProgress: Boolean
) {
    val kgValue = (0..300).map { it.toString() }
    val repValue = (0..100).map { it.toString() }

    if(inProgress) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .pointerInput(Unit) {  }
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.9f)
            .background(color = Color.LightGray)
            .padding(10.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(color = Color.Gray),
                contentAlignment = Alignment.Center
            ){
                Text(text = "Kg",textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            InfiniteItemsPicker(
                items = kgValue,
                firstIndex = (301 * 200)+preWeight - 1,
                onItemSelected =  onWeightChosen,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(0.66f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(color = Color.Gray),
                contentAlignment = Alignment.Center
            ){
                Text(text = "Rep")
            }
            InfiniteItemsPicker(
                items = repValue,
                firstIndex = (101 * 200) + preRep - 1,
                onItemSelected =  onRepChosen,
            )
        }
    }
}


@Composable
fun InfiniteItemsPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    firstIndex: Int,
    onItemSelected: (String) -> Unit,
) {
    // 얼마나 내렸는지 기억
    val listState = rememberLazyListState(firstIndex)
    val currentValue = remember { mutableStateOf("") }
    val previousValue = remember { mutableStateOf("$firstIndex")}

    LaunchedEffect(key1 = firstIndex) {
        if (firstIndex.toString() != currentValue.value) {
            val newPosition = (items.size * 30) + firstIndex

            listState.scrollToItem(newPosition)
        }
    }

    LaunchedEffect(key1 = !listState.isScrollInProgress) {
        if(previousValue.value != currentValue.value) {
            onItemSelected(currentValue.value)
            listState.animateScrollToItem(index = listState.firstVisibleItemIndex)
            previousValue.value = currentValue.value
        }
    }

    Box(
        modifier = Modifier
            .height(106.dp)
            .fillMaxWidth()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
            content = {
                items(count = Int.MAX_VALUE, itemContent = {
                    val index = it % items.size
                    if (it == listState.firstVisibleItemIndex + 1) {
                        currentValue.value = items[index]
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    var isEditing by remember { mutableStateOf(false) }
                    var Text by remember { mutableStateOf(String()) }

                    if (isEditing) {
                        var newText by remember { mutableStateOf(Text) }
                        val coroutineScope = rememberCoroutineScope() // CoroutineScope 생성

                        Box(
                            modifier = Modifier.wrapContentSize(unbounded = true, align = Alignment.TopStart)
                        ) {
                            TextField(
                                value = newText,
                                onValueChange = {
                                    newText = it
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number, // 숫자 입력 모드 설정
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        isEditing = false
                                        if (newText.isNotEmpty()) {
                                            val newPosition =
                                                (items.size * 30) + newText.toInt() - 1

                                            // CoroutineScope 내에서 scrollToItem 호출
                                            coroutineScope.launch {
                                                listState.scrollToItem(newPosition)
                                            }
                                        }
                                    }
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                singleLine = true,
                                modifier = Modifier
                                    .width(80.dp)
                                    .fillMaxHeight()
                            )
                        }
                    } else {
                        Text(
                            text = items[index],
                            modifier = Modifier
                                .alpha(if (it == listState.firstVisibleItemIndex + 1) 1f else 0.3f)
                                .background(Color(if (it == listState.firstVisibleItemIndex + 1) 0xFFDDE2FD else 0xFFFFFFFF))
                                .fillMaxWidth()
                                .clickable(
                                    enabled = it == listState.firstVisibleItemIndex + 1,
                                    onClick = {
                                        isEditing = true
                                        Text = items[index]
                                    }
                                ),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                })
            }
        )
    }
}

//--------------------------------------------

// 최신값
@Composable
fun EMGCollector(emgUiState: EmgUiState, isStarting:Boolean, planInfo: SetProgress, addCnt: () -> Unit, inProgress: Boolean, muscleAverage: Double) {
    var signal_1 by remember { mutableStateOf(0) }
    var signal_2 by remember { mutableStateOf(0) }
    var signal_3 by remember { mutableStateOf(0) }
    var signal_4 by remember { mutableStateOf(0) }

    var lastLev by remember { mutableStateOf(0)}
    var lastTime by remember { mutableStateOf<Long>(0)}
    var currentLev by remember { mutableStateOf(0) }

    // CoroutineScope을 만듭니다.
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(emgUiState.emg1Avg) {
        if(inProgress) {
            val curTime = System.currentTimeMillis()

            if(curTime - lastTime >= 500) {
                Log.d("cnt", "currentLev: $currentLev, lastLev: $lastLev, avg: ${emgUiState.emg1Avg}")
                currentLev = ((emgUiState.emg1Avg / 90) + 1).toInt()
                if (currentLev >= 3 && lastLev < 3) {
                    addCnt()
                    Log.d("cnt", "triggered")
                }
                lastLev = currentLev

                lastTime = curTime
            }
        }
    }

    LaunchedEffect(isStarting) {
        while (isStarting) {
            // 0.05초당 한번씩 업데이트
            delay(5)

            signal_1++;
            if(signal_1%2==0)signal_2++;
            if(signal_1%3==0)signal_3++;
            if(signal_1%4==0)signal_4++;
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .background(Color.White),
                contentAlignment = Alignment.Center
            ){
                Text("Lv. ${String.format("%.3f", (emgUiState.emg1Avg / 90) + 1)}")
                Box(modifier = Modifier
                    .clip(CircleShape)
                    .size((emgUiState.emg1Avg % 90).dp)
                    .background(
                        when ((emgUiState.emg1Avg / 90).toInt()) {
                            0 -> Color.White.copy(0.7f)
                            1 -> Color.Red.copy(0.7f)
                            2 -> Color.Green.copy(0.7f)
                            else -> Color.Blue.copy(0.7f)
                        }
                    )
                    .wrapContentSize(Alignment.Center)
                )
            }
            Box(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White),
                contentAlignment = Alignment.Center
            ){
                Text("Lv. ${String.format("%.3f", (emgUiState.emg2Avg / 90) + 1)}")
                Box(modifier = Modifier
                    .clip(CircleShape)
                    .size((emgUiState.emg2Avg % 90).dp)
                    .background(
                        when ((emgUiState.emg2Avg / 90).toInt()) {
                            0 -> Color.White.copy(0.7f)
                            1 -> Color.Red.copy(0.7f)
                            2 -> Color.Green.copy(0.7f)
                            else -> Color.Blue.copy(0.7f)
                        }
                    )
                    .wrapContentSize(Alignment.Center)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ){
            Box(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .background(Color(0xFFDDE2FD)),
                contentAlignment = Alignment.Center
            ){
                Text(String.format("개수 : ${planInfo.successRep}", emgUiState.emg1Avg % 90))
            }
            Box(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color(0xFFDDE2FD)),
                contentAlignment = Alignment.Center
            ){
                Text("${String.format("%.3f", muscleAverage)}")
            }
        }
    }
}

//-----------------------------------------------------

//@Preview(showBackground = true)
//@Composable
//fun PreviewEMGScreen(){
//    Column {
//        ExerciseEMGScreen()
//        FFT_ready(24)
//
//    }
//}
@Composable
//build.gradle에 //implementation ("com.github.wendykierp:JTransforms:3.1")//넣자
fun FFT_ready(N:Int){//N은 신호의 갯수
    val y = DoubleArray(N) //허수값 0

    for (i in 0 until N) {
        y[i] = 0.0
    }

    val x = DoubleArray(N) //실수값 여기에 신호를 넣자

    for (i in 0 until N) {
        x[i] = Math.sin(2 * Math.PI * 24 * 0.004 * i) + Math.sin(2 * Math.PI * 97 * 0.004 * i)
    }

    val a = DoubleArray(2 * N) //fft 수행할 배열 사이즈 2N

    for (k in 0 until N) {
        a[2 * k] = x[k] //Re
        a[2 * k + 1] = y[k] //Im
    }

    val fft = DoubleFFT_1D(N.toLong()) //1차원의 fft 수행

    fft.complexForward(a) //a 배열에 output overwrite


    val mag = DoubleArray(N / 2)
    var sum = 0.0
    for (k in 0 until N / 2) {
        mag[k] = Math.sqrt(Math.pow(a[2 * k], 2.0) + Math.pow(a[2 * k + 1], 2.0))
        sum += mag[k]
    }
    val average = DoubleArray(N / 2)
    var nowSum = 0.0
    for (k in 0 until N / 2) {
        nowSum+=mag[k];
        average[k]=nowSum/sum
    }

    val avrNumbers = average.map { it as Number }.toTypedArray()
}