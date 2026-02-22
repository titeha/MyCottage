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

private data class AppTab(
  val route: String,
  val title: String,
  val icon: ImageVector,
)

private val tabs = listOf(
  AppTab("today", "Сегодня", Icons.Filled.Home),
  AppTab("calendar", "Календарь", Icons.Filled.CalendarMonth),
  AppTab("plantings", "Посадки", Icons.Filled.Spa),
  AppTab("plan", "План", Icons.Filled.Map),
)

@Composable
fun AppRoot() {
  val navController = rememberNavController()
  val isTabletLike = LocalConfiguration.current.smallestScreenWidthDp >= 600

  if (isTabletLike) {
    TabletLayout(navController)
  } else {
    PhoneLayout(navController)
  }
}

@Composable
private fun PhoneLayout(navController: NavHostController) {
  Scaffold(
    bottomBar = { BottomBar(navController) }
  ) { padding ->
    AppNavHost(navController, Modifier.fillMaxSize())
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
        icon = { Icon(tab.icon, contentDescription = tab.title) },
        label = { Text(tab.title) }
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
        icon = { Icon(tab.icon, contentDescription = tab.title) },
        label = { Text(tab.title) }
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
    composable("today") { PlaceholderScreen("Сегодня") }
    composable("calendar") { PlaceholderScreen("Календарь") }
    composable("plantings") { PlaceholderScreen("Посадки") }
    composable("plan") { PlaceholderScreen("План участка") }
  }
}

@Composable
private fun PlaceholderScreen(title: String) {
  Surface(Modifier.fillMaxSize()) {
    Text(text = title, style = MaterialTheme.typography.headlineMedium)
  }
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
