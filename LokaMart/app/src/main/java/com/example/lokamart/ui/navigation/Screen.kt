package com.example.lokamart.ui.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object OtpVerification : Screen("otp_verification")
    data object Home : Screen("home")

    // nanti bisa lanjut:
    // data object Profile : Screen("profile")
    // data object ProductList : Screen("product_list")
}