package com.example.lokamart.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lokamart.ui.screen.auth.GreenDark

// ── Bottom Nav Items ──────────────────────────────────────────
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Beranda : BottomNavItem("home", "Beranda", Icons.Outlined.Home)
    object Favorit : BottomNavItem("wishlist", "Favorit", Icons.Outlined.FavoriteBorder)
    object Pesanan : BottomNavItem("orders", "Pesanan", Icons.Outlined.ListAlt)
    object Profil  : BottomNavItem("profile", "Profil", Icons.Outlined.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Beranda,
    BottomNavItem.Favorit,
    BottomNavItem.Pesanan,
    BottomNavItem.Profil
)

// ── Bottom Navigation Bar ─────────────────────────────────────
@Composable
fun LokaMartBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Box(
                        modifier = if (selected) Modifier
                            .clip(CircleShape)
                            .background(GreenDark.copy(alpha = 0.12f))
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                        else Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) GreenDark else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) GreenDark else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// ── Header tanpa tombol back (Beranda, Profil) ────────────────
@Composable
fun LokaMartHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "LokaMart",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = GreenDark
        )
    }
}

// ── Header dengan tombol back (Detail Profile, dll) ───────────
@Composable
fun LokaMartHeaderWithBack(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = GreenDark
            )
        }
        Text(
            text = "LokaMart",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = GreenDark
        )
    }
}
