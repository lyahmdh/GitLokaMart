package com.example.lokamart.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lokamart.ui.navigation.Screen
import com.example.lokamart.ui.components.BottomBar.LokaMartBottomBar
import com.example.lokamart.ui.navigation.LokaMartNavGraph

@Composable
fun MainScreen(
    navController: NavHostController
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomRoutes = listOf(
        Screen.Home.route,
        Screen.Favorite.route,
        Screen.Order.route,
        Screen.Profile.route
    )

    Scaffold(

        bottomBar = {
            if (currentRoute in bottomRoutes) {
                LokaMartBottomBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }

    ) { padding ->

        LokaMartNavGraph(
            navController = navController,
            modifier = if (currentRoute in bottomRoutes)
                Modifier.padding(padding)
            else
                Modifier
        )
    }
}