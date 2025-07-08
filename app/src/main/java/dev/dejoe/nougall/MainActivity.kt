package dev.dejoe.nougall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.collection.arraySetOf
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import dev.dejoe.nougall.composables.FavoritesScreen
import dev.dejoe.nougall.composables.HomeScreen
import dev.dejoe.nougall.composables.MovieDetailsScreenRoute
import dev.dejoe.nougall.composables.SettingsScreen
import dev.dejoe.nougall.composables.TrendingMoviesListScreen
import dev.dejoe.nougall.ui.custom.TimeWindow
import dev.dejoe.nougall.ui.theme.MyApplicationTheme

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen(
        Constants.Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home
    )

    object Favorites : Screen(
        Constants.Screen.Favorites, "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder
    )

    object Settings : Screen(
        Constants.Screen.Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings
    )
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                NougallApp()
            }
        }
    }
}

@Composable
fun NougallApp() {
    MainScreen()
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(navController)
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Constants.Screen.movieDetailsRoute(movieId))
                    },
                    onViewMoreClick = { timeWindow ->
                        navController.navigate(Constants.Screen.trendingListRoute(timeWindow.apiParam))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Constants.Screen.movieDetailsRoute(movieId))
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(route = Constants.Screen.TrendingList) {
                TrendingMoviesListScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Constants.Screen.movieDetailsRoute(movieId))
                    }
                )

            }
            composable(
                route = Constants.Screen.TrendingListWithArg,
                arguments = listOf(
                    navArgument("timeWindow") {
                        type = NavType.StringType
                        defaultValue = TimeWindow.Today.apiParam
                    }
                )
            ) { backStackEntry ->
                val timeWindowParam = backStackEntry.arguments?.getString("timeWindow")
                val initialTimeWindow = TimeWindow.fromApiParam(timeWindowParam)

                TrendingMoviesListScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Constants.Screen.movieDetailsRoute(movieId))
                    },
                    selectedTimeWindow = initialTimeWindow
                )
            }
            composable(
                route = "details/{movieId}",
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                MovieDetailsScreenRoute(movieId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val topLevelRoutes = arraySetOf(
        Screen.Home.route, Screen.Favorites.route, Screen.Settings.route
    )

    val showBackButton = currentRoute != null && currentRoute !in topLevelRoutes

    TopAppBar(
        title = {
            Text(
                when (currentRoute) {
                    Screen.Home.route -> "Home"
                    Screen.Favorites.route -> "Favorites"
                    Screen.Settings.route -> "Settings"
                    "details" -> "Details"
                    else -> "MokTMDB"
                }
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavController) {
    val screens = arrayOf(
        Screen.Home, Screen.Favorites, Screen.Settings
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            val selected = currentDestination?.route == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}