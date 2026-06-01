package com.example.lokamart.ui.navigation

sealed class Screen(
    val route: String
) {

    // Auth
    data object Splash : Screen("splash")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object OtpVerification : Screen("otp_verification")


    // Main
    data object Home : Screen("home")

    data object Favorite : Screen("favorite")

    data object Order : Screen("orders")

    data object Profile : Screen("profile")


    // Product
    data object ProductDetail : Screen("product_detail/{productId}") {

        fun createRoute(
            productId: String
        ) = "product_detail/$productId"
    }


    // Seller
    data object ManageProducts : Screen("manage_products")

    data object CreateProduct : Screen("create_product")

    data object EditProduct : Screen("edit_product/{productId}") {

        fun createRoute(
            productId: String
        ) = "edit_product/$productId"
    }
}