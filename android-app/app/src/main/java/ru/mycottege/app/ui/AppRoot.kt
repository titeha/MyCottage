package ru.mycottege.app.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import ru.mycottege.app.legal.DisclaimerDialog
import ru.mycottege.app.legal.LegalPrefs
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import ru.mycottege.app.R
import androidx.compose.material.icons.filled.Settings
import ru.mycottege.app.ui.screens.SettingsScreen
import ru.mycottege.app.ui.common.AppScreen
import ru.mycottege.app.ui.screens.PlantingsScreen
import ru.mycottege.app.ui.screens.TodayScreen
import ru.mycottege.app.ui.screens.CalendarScreen

private data class AppTab(
  val route: String,
  @StringRes val titleRes: Int,
  val icon: ImageVector,
)

private val tabs = listOf(
  AppTab("today", R.string.tab_today, Icons.Filled.Home),
  AppTab("calendar", R.string.tab_calendar, Icons.Filled.CalendarMonth),
  AppTab("plantings", R.string.tab_plantings, Icons.Filled.Spa),
  AppTab("plan", R.string.tab_plan, Icons.Filled.Map),
  AppTab("settings", R.string.tab_settings, Icons.Filled.Settings),
)

@Composable
fun AppRoot() {
  val context = LocalContext.current
  val legalPrefs = remember { LegalPrefs(context) }
  val scope = rememberCoroutineScope()
  val accepted by legalPrefs.isDisclaimerAccepted.collectAsState(initial = false)

  if (!accepted) {
    DisclaimerDialog(
      onAccept = { scope.launch { legalPrefs.acceptDisclaimer() } }
    )
    return
  }

  val navController = rememberNavController()
  val isTabletLike = LocalConfiguration.current.smallestScreenWidthDp >= 600

  if (isTabletLike) {
    TabletLayout(navController)
  } else {
    PhoneLayout(navController)
  }
}

@Composable
private fun TabletLayout(navController: NavHostController) {
  Row(Modifier.fillMaxSize()) {
    NavigationRailBar(navController)
    AppNavHost(navController, Modifier.fillMaxSize())
  }
}

@Composable
private fun BottomBar(navController: NavHostController) {
  NavigationBar {
    tabs.forEach { tab ->
      val selected = navController.currentDestinationRoute() == tab.route
      NavigationBarItem(
        selected = selected,
        onClick = { navController.navigateSingleTop(tab.route) },
        icon = { Icon(tab.icon, contentDescription = stringResource(tab.titleRes)) },
        label = { Text(stringResource(tab.titleRes)) }
      )
    }
  }
}

@Composable
private fun NavigationRailBar(navController: NavHostController) {
  NavigationRail {
    tabs.forEach { tab ->
      val selected = navController.currentDestinationRoute() == tab.route
      NavigationRailItem(
        selected = selected,
        onClick = { navController.navigateSingleTop(tab.route) },
        icon = { Icon(tab.icon, contentDescription = stringResource(tab.titleRes)) },
        label = { Text(stringResource(tab.titleRes)) }
      )
    }
  }
}

@Composable
private fun AppNavHost(navController: NavHostController, modifier: Modifier) {
  NavHost(
    navController = navController,
    startDestination = "today",
    modifier = modifier
  ) {
    composable("today") { TodayScreen() }
    composable("calendar") { CalendarScreen() }
    composable("plantings") { PlantingsScreen() }
    composable("plan") { PlaceholderScreen(R.string.tab_plan) }
    composable("settings") { SettingsScreen() }
  }
}

@Composable
private fun PlaceholderScreen(@StringRes titleRes: Int) {
  AppScreen(titleRes)
}

@Composable
private fun PhoneLayout(navController: NavHostController) {
  Scaffold(
    bottomBar = { BottomBar(navController) }
  ) { padding ->
    AppNavHost(
      navController,
      Modifier.fillMaxSize().padding(padding)
    )
  }
}

// Вспомогательные расширения (без внешних зависимостей)
private fun NavHostController.currentDestinationRoute(): String? {
  return this.currentBackStackEntry?.destination?.route
}

private fun NavHostController.navigateSingleTop(route: String) {
  this.navigate(route) {
    launchSingleTop = true
    restoreState = true
    popUpTo(this@navigateSingleTop.graph.startDestinationId) { saveState = true }
  }
}
