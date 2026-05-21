package com.example.lokamart.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lokamart.ui.screen.auth.AuthViewModel
import com.example.lokamart.ui.screen.auth.LoginScreen
import com.example.lokamart.ui.screen.auth.RegisterScreen
import com.example.lokamart.ui.screen.auth.SplashScreen
import com.example.lokamart.ui.screen.home.HomeScreen

// ── Semua route aplikasi ──────────────────────────────────────
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    // Tambahkan route lain di sini nanti:
    // const val STORE_MANAGEMENT = "store_management"
    // const val PRODUCT_LIST = "product_list"
    // const val EXPLORE = "explore"
    // const val WISHLIST = "wishlist"
    // const val PURCHASE_HISTORY = "purchase_history"
    // const val PROFILE = "profile"
}

@Composable
fun LokaMartNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Tentukan start destination berdasarkan status login
    val startDestination = if (uiState.isLoggedIn) Routes.HOME else Routes.SPLASH

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ── Splash / Onboarding ───────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinish = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ─────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        // Hapus login dari back stack agar tombol back tidak kembali ke login
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Register ──────────────────────────────────────────
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigateUp()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Home (placeholder, nanti diganti dengan Bottom Nav) ──
        composable(Routes.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
