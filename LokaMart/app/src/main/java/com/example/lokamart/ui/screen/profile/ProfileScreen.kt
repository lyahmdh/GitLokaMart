package com.example.lokamart.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokamart.ui.screen.auth.GreenDark
import com.example.lokamart.ui.viewmodel.ProfileViewModel

private val GreenLight = Color(0xFFE8F5E9)
private val TextSecondary = Color(0xFF757575)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToManageProducts: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Trigger logout navigation
    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) onLogout()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Header ────────────────────────────────────────────
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenDark)
                }
            }

            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.errorMessage!!, color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = viewModel::loadProfile,
                            colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
                        ) { Text("Coba Lagi") }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ── Profile Card ──────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF9E9E9E)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Name
                            Text(
                                text = uiState.profile?.name ?: "-",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            // Email row
                            EditableInfoRow(
                                icon = Icons.Outlined.Email,
                                value = uiState.profile?.email ?: "-",
                                isEditing = false, // email tidak bisa diedit
                                editValue = "",
                                onEditClick = null,
                                onValueChange = {},
                                onSave = {},
                                onCancel = {}
                            )

                            Spacer(Modifier.height(4.dp))

                            // Phone row
                            EditableInfoRow(
                                icon = Icons.Outlined.Phone,
                                value = uiState.profile?.phone
                                    ?.ifBlank { "Belum diisi" } ?: "Belum diisi",
                                isEditing = uiState.isEditingPhone,
                                editValue = uiState.editPhoneValue,
                                onEditClick = viewModel::startEditPhone,
                                onValueChange = viewModel::onPhoneChange,
                                onSave = viewModel::savePhone,
                                onCancel = viewModel::cancelEditPhone,
                                isSaving = uiState.isSaving
                            )

                            // Edit name dialog
                            if (uiState.isEditingName) {
                                EditNameDialog(
                                    value = uiState.editNameValue,
                                    onValueChange = viewModel::onNameChange,
                                    onSave = viewModel::saveName,
                                    onDismiss = viewModel::cancelEditName,
                                    isSaving = uiState.isSaving
                                )
                            }
                        }
                    }

                    // ── Kelola Produk ─────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenDark),
                        elevation = CardDefaults.cardElevation(2.dp),
                        onClick = onNavigateToManageProducts
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Store,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Kelola Produk",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    "Pantau produk",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    // ── Keluar Akun ───────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        onClick = viewModel::logout
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Outlined.Logout,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Keluar Akun",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// EditableInfoRow — baris info yang bisa diedit inline
// ─────────────────────────────────────────────────────────────
@Composable
private fun EditableInfoRow(
    icon: ImageVector,
    value: String,
    isEditing: Boolean,
    editValue: String,
    onEditClick: (() -> Unit)?,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isSaving: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = editValue,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDark,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                trailingIcon = {
                    Row {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = GreenDark,
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = onSave) {
                                Icon(Icons.Outlined.Check, null, tint = GreenDark)
                            }
                            IconButton(onClick = onCancel) {
                                Icon(Icons.Outlined.Close, null, tint = Color.Gray)
                            }
                        }
                    }
                }
            )
        } else {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(value, fontSize = 14.sp, color = TextSecondary)
            if (onEditClick != null) {
                Spacer(Modifier.width(6.dp))
                IconButton(onClick = onEditClick, modifier = Modifier.size(20.dp)) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = GreenDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// EditNameDialog
// ─────────────────────────────────────────────────────────────
@Composable
private fun EditNameDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    isSaving: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Nama", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDark,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
            ) {
                if (isSaving) CircularProgressIndicator(Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextSecondary) }
        }
    )
}