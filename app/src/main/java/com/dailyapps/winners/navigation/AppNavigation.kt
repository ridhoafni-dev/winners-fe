package com.dailyapps.winners.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.common.utils.toLocalDateTime
import com.dailyapps.feature.absent.AbsentScreen
import com.dailyapps.feature.absent.AbsentViewModel
import com.dailyapps.feature.activty_plan.ActivityPlanViewModel
import com.dailyapps.feature.activty_plan.page.detail.ActivityPlanDetailScreen
import com.dailyapps.feature.activty_plan.page.form.ActivityPlanFormScreen
import com.dailyapps.feature.activty_plan.page.list.ActivityPlanListScreen
import com.dailyapps.feature.auth.MainViewModel
import com.dailyapps.feature.auth.ui.screen.AuthViewModel
import com.dailyapps.feature.auth.ui.screen.login.LoginScreen
import com.dailyapps.feature.auth.ui.screen.register.RegisterScreen
import com.dailyapps.feature.exam.ExamViewModel
import com.dailyapps.feature.exam.pages.detail.DetailExamPage
import com.dailyapps.feature.exam.pages.list.ListExamPage
import com.dailyapps.feature.exam.pages.questions.ExamQuestionPage
import com.dailyapps.feature.exam.pages.questions.ExamQuestionViewModel
import com.dailyapps.feature.exam.pages.token.TokenPage
import com.dailyapps.feature.exam.pages.token.TokenPageViewModel
import com.dailyapps.feature.note.NoteScreen
import com.dailyapps.feature.note.NoteViewModel
import com.dailyapps.feature.observation.ObservationViewModel
import com.dailyapps.feature.observation.page.detail.ObservationDetailScreen
import com.dailyapps.feature.observation.page.form.ObservationFormScreen
import com.dailyapps.feature.observation.page.list.ObservationListScreen
import com.dailyapps.feature.report.ReportViewModel
import com.dailyapps.feature.report.page.list.ReportListScreen
import com.dailyapps.home.HomeViewModel
import com.dailyapps.home.changepassword.ChangePasswordScreen
import com.dailyapps.home.dashboard.HomeScreen
import com.dailyapps.home.profile.ProfileDetailScreen
import com.dailyapps.home.profile.ProfileScreen
import com.dailyapps.home.visi.VisiMisiScreen
import com.dailyapps.score.ui.screen.ScoreViewModel
import com.dailyapps.score.ui.screen.detail.ScoreDetailScreen
import com.dailyapps.score.ui.screen.main.ScoreScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberAnimatedNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.getIsLoggedInUser()
    }

    isLoggedIn?.let { loggedIn ->
        AnimatedNavHost(
            navController = navController,
            startDestination = if (loggedIn) NavRoute.homeScreen else NavRoute.loginScreen
        ) {
            composableWithSlideAnimation(
                route = NavRoute.loginScreen
            ) {
                val viewModel: AuthViewModel = hiltViewModel()
                LoginScreen(navController, viewModel)
            }
            composableWithSlideAnimation(
                route = NavRoute.registerScreen
            ) {
                RegisterScreen(navController)
            }
            composableWithSlideAnimation(
                route = NavRoute.homeScreen
            ) {
                val homeViewModel: HomeViewModel = hiltViewModel()

                HomeScreen(navController, homeViewModel)
            }
            composableWithSlideAnimation(
                route = NavRoute.changePasswordScreen
            ) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                ChangePasswordScreen(navController, homeViewModel)
            }
            composableWithSlideAnimation(
                route = NavRoute.profileScreen,
            ) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                ProfileScreen(navController, homeViewModel)
            }
            composableWithSlideAnimation(
                route = NavRoute.profileDetailScreen
            ) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                ProfileDetailScreen(navController, homeViewModel)
            }
            composableWithSlideAnimation(
                route = NavRoute.absentScreen
            ) {
                val absentViewModel: AbsentViewModel = hiltViewModel()
                AbsentScreen(navController, absentViewModel)
            }
            composableWithSlideAnimation(
                route = NavRoute.noteScreen
            ) {
                val noteViewModel: NoteViewModel = hiltViewModel()
                NoteScreen(navController, noteViewModel)
            }
            composableWithSlideAnimation(
                route = NavRoute.scoreScreen
            ) {
                val scoreViewModel: ScoreViewModel = hiltViewModel()
                ScoreScreen(navController, scoreViewModel)
            }
            composableWithSlideAnimation(
                route = "${NavRoute.scoreDetailScreen}/{matpelId}",
                arguments = listOf(navArgument("matpelId") { type = NavType.IntType }),
            ) { navBackStackEntry ->
                val scoreViewModel: ScoreViewModel = hiltViewModel()
                val matpelId = navBackStackEntry.arguments?.getInt("matpelId")
                ScoreDetailScreen(matpelId ?: 0, navController, scoreViewModel)
            }
            composableWithSlideAnimation(NavRoute.listExamScreen) {
                val examViewModel: ExamViewModel = hiltViewModel()
                ListExamPage(navController, examViewModel)
            }
            composableWithSlideAnimation(
                route = "${NavRoute.detailExamScreen}/{semester}/{jenisUjian}",
                arguments = listOf(
                    navArgument("semester") { type = NavType.StringType },
                    navArgument("jenisUjian") { type = NavType.StringType },
                ),
            ) { navBackStackEntry ->
                val examViewModel: ExamViewModel = hiltViewModel()
                val semester = navBackStackEntry.arguments?.getString("semester")
                val jenisUjian = navBackStackEntry.arguments?.getString("jenisUjian")
                DetailExamPage(semester ?: "", jenisUjian ?: "", navController, examViewModel)
            }
            composableWithSlideAnimation(
                route = "${NavRoute.tokeExamScreen}/{token}/{authToken}/{ujianId}/{siswaId}/{ujianEnd}/{matpel}",
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType },
                    navArgument("authToken") { type = NavType.StringType },
                    navArgument("ujianId") { type = NavType.IntType },
                    navArgument("siswaId") { type = NavType.IntType },
                    navArgument("ujianEnd") { type = NavType.StringType },
                    navArgument("matpel") { type = NavType.StringType },
                ),
            ) { navBackStackEntry ->
                val tokenPageViewModel: TokenPageViewModel = hiltViewModel()
                val token = navBackStackEntry.arguments?.getString("token") ?: ""
                val authToken = navBackStackEntry.arguments?.getString("authToken") ?: ""
                val ujianId = navBackStackEntry.arguments?.getInt("ujianId") ?: 0
                val siswaId = navBackStackEntry.arguments?.getInt("siswaId") ?: 0
                val ujianEnd = navBackStackEntry.arguments?.getString("ujianEnd") ?: ""
                val matpel = navBackStackEntry.arguments?.getString("matpel") ?: ""
                TokenPage(
                    navController, tokenPageViewModel, token, authToken, ujianId, siswaId, ujianEnd, matpel
                )
            }

            composableWithSlideAnimation(
                route = "${NavRoute.testExamScreen}/{authToken}/{ujianId}/{siswaId}/{ujianEnd}/{matpel}",
                arguments = listOf(
                    navArgument("authToken") { type = NavType.StringType },
                    navArgument("ujianId") { type = NavType.IntType },
                    navArgument("siswaId") { type = NavType.IntType },
                    navArgument("ujianEnd") { type = NavType.StringType },
                    navArgument("matpel") { type = NavType.StringType },
                )
            ) { navBackStackEntry ->
                val examQuestionViewModel: ExamQuestionViewModel = hiltViewModel()

//                val token = "Bearer 14|TscDreENbR5uPxHZ5ByU1Dr8XSRI9jbdZvT0Y9mQ"
//                val ujianId = 14
//                val siswaId = 25
//                val ujianEnd = getCurrentWibLocalDateTime().plusMinutes(1)
//                val matpel = "Kimia"

                val token = navBackStackEntry.arguments?.getString("authToken") ?: ""
                val ujianId = navBackStackEntry.arguments?.getInt("ujianId") ?: 0
                val siswaId = navBackStackEntry.arguments?.getInt("siswaId") ?: 0
                val ujianEnd = (navBackStackEntry.arguments?.getString("ujianEnd") ?: "").toLocalDateTime()
                val matpel = navBackStackEntry.arguments?.getString("matpel") ?: ""
                ExamQuestionPage(
                    navController,
                    examQuestionViewModel,
                    token,
                    ujianId,
                    siswaId,
                    ujianEnd,
                    matpel
                )
            }

            composableWithSlideAnimation(
                route = NavRoute.visiMisiScreen
            ) {
                VisiMisiScreen(navController)
            }

            composableWithSlideAnimation(
                route = NavRoute.observationScreen,
            ) { _ ->
                val observationViewModel: ObservationViewModel = hiltViewModel()

                ObservationListScreen(
                    navController,
                    observationViewModel
                )
            }

            composableWithSlideAnimation(
                route = "${NavRoute.observationDetailScreen}/{observationId}",
                arguments = listOf(
                    navArgument("observationId") { type = NavType.IntType }
                )
            ) { navBackStackEntry ->
                val observationViewModel: ObservationViewModel = hiltViewModel()
                val observationId = navBackStackEntry.arguments?.getInt("observationId") ?: 0

                ObservationDetailScreen(
                    navController,
                    observationId.toLong(),
                    observationViewModel
                )
            }

            composableWithSlideAnimation(
                route = "${NavRoute.formObservationScreen}/{observationId}",
                arguments = listOf(
                navArgument("observationId") { type = NavType.IntType }
                )
            ) { navBackStackEntry ->
                val observationViewModel: ObservationViewModel = hiltViewModel()
                val observationId = navBackStackEntry.arguments?.getInt("observationId") ?: 0

                ObservationFormScreen(
                    navController,
                    observationViewModel,
                    observationId.toLong(),
                )
            }

            composableWithSlideAnimation(
                route = "${NavRoute.formActivityPlanScreen}/{activityPlanId}",
                arguments = listOf(
                navArgument("activityPlanId") { type = NavType.IntType }
                )
            ) { navBackStackEntry ->
                val activityPlanViewModel: ActivityPlanViewModel = hiltViewModel()
                val activityPlanId = navBackStackEntry.arguments?.getInt("activityPlanId") ?: 0

                ActivityPlanFormScreen(
                    navController,
                    activityPlanViewModel,
                    activityPlanId.toLong(),
                )
            }

            composableWithSlideAnimation(
                route = "${NavRoute.activityPlanDetailScreen}/{activityPlanId}",
                arguments = listOf(
                    navArgument("activityPlanId") { type = NavType.IntType }
                )
            ) { navBackStackEntry ->
                val activityPlanViewModel: ActivityPlanViewModel = hiltViewModel()
                val activityPlanId = navBackStackEntry.arguments?.getInt("activityPlanId") ?: 0

                ActivityPlanDetailScreen(
                    navController,
                    activityPlanId.toLong(),
                    activityPlanViewModel
                )
            }

            composableWithSlideAnimation(
                route = NavRoute.activityPlanScreen,
            ) { _ ->
                val activityPlanViewModel: ActivityPlanViewModel = hiltViewModel()

                ActivityPlanListScreen(
                    navController,
                    activityPlanViewModel
                )
            }

            composableWithSlideAnimation(
                route = NavRoute.reportScreen,
            ) { _ ->
                val reportViewModel: ReportViewModel = hiltViewModel()

                ReportListScreen(
                    navController,
                    reportViewModel
                )
            }

        }
    }
}