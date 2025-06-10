package com.dailyapps.home.dashboard

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.HomeMenu
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.data.utils.DummyDatasource
import com.dailyapps.home.HomeViewModel
import com.dailyapps.home.R
import com.dailyapps.home.components.HomeProfile
import com.dailyapps.home.components.VisiMisi

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {

    val context = LocalContext.current
    val selected by remember { mutableStateOf("") }

    val user by homeViewModel.user.collectAsState()

    var backCounter by remember { mutableIntStateOf(1) }

    BackHandler(backCounter > 0) {
        Toast.makeText(context, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
        backCounter--
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            HomeProfile(modifier = Modifier.padding(top = 16.dp), user = user) {
                navController.navigate(NavRoute.profileScreen)
            }
        }

        item {
            VisiMisi(modifier = Modifier.padding(top = 24.dp)) {
                navController.navigate(NavRoute.visiMisiScreen)
            }
        }

        item {
            BaseText(
                text = stringResource(R.string.all_menu),
                fontFamily = FontType.MEDIUM,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
        }

        item {
            ListHomeMenu(selected, navController)
        }

    }

}

@Composable
fun ListHomeMenu(selected: String, navController: NavHostController) {
    LazyVerticalGrid(
        modifier = Modifier.height(320.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(DummyDatasource.generateMenus()) { menu ->
            HomeMenu(
                menu = menu,
                onValueChange = {/*selected = menu.title*/ },
                selected = selected == menu.title,
                onItemClick = { _menu ->
                    when (_menu.title) {
                        "Observation" -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(NavRoute.observationScreen)
                            }, 200)
                        }

                        "Reports" -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(NavRoute.registerScreen)
                            }, 200)
                        }

                        "Memos" -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(NavRoute.scoreScreen)
                            }, 200)
                        }

                        "Planned Activities" -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(NavRoute.activityPlanScreen)
                            }, 200)
                        }

                        "Self Reflection" -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(NavRoute.listExamScreen)
                            }, 200)
                        }
                    }
                }
            )
        }
    }
}

fun Modifier.scrollEnabled(
    enable: Boolean,
) = nestedScroll(
    connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
            if (enable) Offset.Zero else available
    }
)

/*
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, name = "Dark")
@Preview(uiMode = UI_MODE_NIGHT_NO, showBackground = true, name = "Light")
@Composable
fun HomeScreenPreview() {
    val navHostController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    ISekolahTheme {
        HomeScreen(navHostController, homeViewModel)
    }
}*/
