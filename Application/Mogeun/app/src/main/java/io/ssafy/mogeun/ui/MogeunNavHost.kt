package io.ssafy.mogeun.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import io.ssafy.mogeun.ui.screens.routine.addroutine.AddRoutineScreen
import io.ssafy.mogeun.ui.screens.login.LoginScreen
import io.ssafy.mogeun.ui.screens.record.ExerciseDetailScreen
import io.ssafy.mogeun.ui.screens.record.RecordDetailScreen
import io.ssafy.mogeun.ui.screens.record.RecordScreen
import io.ssafy.mogeun.ui.screens.routine.RoutineScreen
import io.ssafy.mogeun.ui.screens.routine.execution.ExecutionScreen
import io.ssafy.mogeun.ui.screens.setting.SettingScreen
import io.ssafy.mogeun.ui.screens.signup.SignupScreen
import io.ssafy.mogeun.ui.screens.summary.SummaryScreen
import io.ssafy.mogeun.ui.screens.routine.addroutine.addexercise.AddExerciseScreen
import io.ssafy.mogeun.ui.screens.routine.addroutine.addexercise.ExplainExerciseScreen

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun MogeunNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Login.route) {
        navigation(route = "Routines", startDestination = Screen.Routine.route) {
            composable(Screen.Routine.route) { RoutineScreen(navController = navController) }
            composable(Screen.Execution.route) { ExecutionScreen() }
        }
        navigation(route = "Records", startDestination = Screen.Record.route) {
            composable(Screen.Record.route) { RecordScreen(navController = navController) }
            composable(Screen.RecordDetail.route) { RecordDetailScreen(navController = navController) }
            composable(Screen.ExerciseDetail.route) { ExerciseDetailScreen() }

        }
        composable(Screen.Summary.route) { SummaryScreen() }
        composable(Screen.Setting.route) { SettingScreen() }
        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.Signup.route) { SignupScreen(navController = navController) }
        composable(Screen.AddRoutine.route) { AddRoutineScreen(navController = navController) }
        composable(Screen.AddExercise.route) { AddExerciseScreen(navController = navController) }

        bottomSheet("Sheet") {
            Text("This is bottom sheet!")
        composable(
            "${Screen.ExplainExercise.route}/{image}",
            arguments = listOf(navArgument("image") { type = NavType.StringType })
        ) { backStackEntry ->
            ExplainExerciseScreen(navController = navController, data = backStackEntry.arguments?.getString("image"))
        }
    }
}