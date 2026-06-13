package com.example.lokamart.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lokamart.ui.viewmodel.AuthViewModel
import com.example.lokamart.ui.viewmodel.ManageProductsViewModel
import com.example.lokamart.ui.screen.auth.LoginScreen
import com.example.lokamart.ui.screen.auth.RegisterScreen
import com.example.lokamart.ui.screen.auth.SplashScreen
import com.example.lokamart.ui.screen.auth.OtpVerificationScreen
import com.example.lokamart.ui.screen.home.HomeScreen
import com.example.lokamart.ui.screen.favorite.FavoriteScreen
import com.example.lokamart.ui.screen.order.OrderHistoryScreen
import com.example.lokamart.ui.screen.profile.ProfileScreen
import com.example.lokamart.ui.screen.product.ProductDetailScreen

import com.example.lokamart.ui.screen.product.ManageProductScreen
import com.example.lokamart.ui.screen.product.CreateProductScreen
import com.example.lokamart.ui.screen.product.EditProductScreen

@Composable
fun LokaMartNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    modifier: Modifier
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (uiState.isLoggedIn) Screen.Home.route else Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigateUp() },
                onRegisterSuccess = {
                    navController.navigate(Screen.OtpVerification.route)
                }
            )
        }

        composable(Screen.OtpVerification.route) {
            OtpVerificationScreen(
                viewModel = authViewModel,
                onVerificationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        composable(Screen.Favorite.route) {
            FavoriteScreen()
        }

        composable(Screen.Order.route) {
            OrderHistoryScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToManageProducts = {
                    navController.navigate(Screen.ManageProducts.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ManageProducts.route) {
            val manageVM: ManageProductsViewModel = viewModel()
            ManageProductScreen(navController = navController, viewModel = manageVM)
        }

        composable(Screen.CreateProduct.route) {
            CreateProductScreen(navController = navController)
        }

        composable(route = Screen.EditProduct.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(
                productId = productId,
                navController = navController
            )
        }

        composable(
            route = Screen.ProductDetail.route
        ) { backStackEntry ->
            val productId =
                backStackEntry.arguments?.getString("productId") ?: ""

            ProductDetailScreen(
                productId = productId,
                navController = navController
            )
        }

//        composable(Screen.ManageProducts.route) {
//            ManageProductScreen(navController)
//        }
//
//        composable(Screen.CreateProduct.route) {
//            CreateProductScreen(navController)
//        }
//
//        composable(
//            route = Screen.EditProduct.route
//        ) { backStackEntry ->
//
//            val productId =
//                backStackEntry.arguments?.getString("productId") ?: ""
//
//            EditProductScreen(
//                productId = productId,
//                navController = navController
//            )
//        }
    }
}