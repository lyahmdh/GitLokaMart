package com.example.lokamart.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokamart.ui.screen.auth.AuthViewModel
import com.example.lokamart.ui.screen.auth.LokaMartGreen

// Placeholder Home — nanti diganti dengan Bottom Navigation + fitur lengkap
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LokaMart",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = LokaMartGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Halo, ${uiState.profile?.name ?: "User"}! 👋",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = uiState.profile?.email ?: "",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Auth berhasil! ✅",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Login & Register sudah berfungsi.\nHalaman ini akan diganti dengan Home + Bottom Nav.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Keluar Akun")
        }
    }
}
