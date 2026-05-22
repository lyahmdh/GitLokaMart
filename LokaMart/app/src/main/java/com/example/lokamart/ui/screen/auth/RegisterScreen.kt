package com.example.lokamart.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokamart.ui.theme.LokaMartGreen
import com.example.lokamart.ui.viewmodel.AuthViewModel

private val BackgroundColor = Color(0xFFEAF3EE)

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agree by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {

        if (
            uiState.successMessage == "Kode OTP telah dikirim"
        ) {

            onRegisterSuccess()
            viewModel.clearMessages()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                bottom = 32.dp
            )
        ) {

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.size(86.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOGO",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(22.dp))
            }

            item {

                Text(
                    text = "Bergabung dengan LokaMart",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LokaMartGreen,
                    textAlign = TextAlign.Center
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {

                Text(
                    text = "Wujudkan kemandirian ekonomi bersama UMKM lokal kebanggaan Indonesia.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 22.sp
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }

            item {

                Card(
                    modifier = Modifier.fillParentMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // NAMA
                        AuthTextField(
                            label = "Nama Lengkap",
                            value = name,
                            onValueChange = {
                                name = it
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // EMAIL
                        AuthTextField(
                            label = "Alamat Email",
                            value = email,
                            onValueChange = {
                                email = it
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // PASSWORD
                        PasswordTextField(
                            label = "Kata Sandi",
                            value = password,
                            visible = passwordVisible,
                            onValueChange = {
                                password = it
                            },
                            onVisibilityChange = {
                                passwordVisible = !passwordVisible
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // CONFIRM PASSWORD
                        PasswordTextField(
                            label = "Konfirmasi Kata Sandi",
                            value = confirmPassword,
                            visible = confirmPasswordVisible,
                            onValueChange = {
                                confirmPassword = it
                            },
                            onVisibilityChange = {
                                confirmPasswordVisible =
                                    !confirmPasswordVisible
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // CHECKBOX
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {

                            Checkbox(
                                checked = agree,
                                onCheckedChange = {
                                    agree = it
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LokaMartGreen
                                )
                            )

                            Text(
                                text = "Saya menyetujui Syarat & Ketentuan serta Kebijakan Privasi LokaMart.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // BUTTON REGISTER
                        Button(
                            onClick = {
                                handleRegister(
                                    name = name,
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    onError = {},
                                    onValid = {
                                        viewModel.register(
                                            name,
                                            email,
                                            password
                                        )
                                    }
                                )
                            },
                            enabled = agree && !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LokaMartGreen,
                                contentColor = Color.White,
                                disabledContainerColor =
                                    LokaMartGreen.copy(alpha = 1f),
                                disabledContentColor = Color.White
                            )
                        ) {

                            if (uiState.isLoading) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )

                            } else {

                                Text(
                                    text = "Daftar Sekarang",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Sudah memiliki akun?",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    TextButton(
                        onClick = onNavigateToLogin
                    ) {

                        Text(
                            text = "Masuk",
                            color = LokaMartGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    Column {

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LokaMartGreen,
                unfocusedBorderColor = Color(0xFFDADADA),
                focusedLabelColor = LokaMartGreen,
                cursorColor = LokaMartGreen,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun PasswordTextField(
    label: String,
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityChange: () -> Unit
) {

    Column {

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            visualTransformation =
                if (visible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            trailingIcon = {

                IconButton(
                    onClick = onVisibilityChange
                ) {

                    Icon(
                        imageVector =
                            if (visible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LokaMartGreen,
                unfocusedBorderColor = Color(0xFFDADADA),
                focusedLabelColor = LokaMartGreen,
                cursorColor = LokaMartGreen,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

// VALIDATION
private fun handleRegister(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    onError: (String) -> Unit,
    onValid: () -> Unit
) {

    if (name.isBlank()) {
        onError("Nama tidak boleh kosong")
        return
    }

    if (email.isBlank()) {
        onError("Email tidak boleh kosong")
        return
    }

    if (password.length < 6) {
        onError("Password minimal 6 karakter")
        return
    }

    if (password != confirmPassword) {
        onError("Kata sandi tidak cocok")
        return
    }

    onValid()
}