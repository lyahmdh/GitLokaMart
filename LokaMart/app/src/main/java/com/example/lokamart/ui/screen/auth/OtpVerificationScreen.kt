package com.example.lokamart.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokamart.ui.theme.LokaMartGreen
import com.example.lokamart.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

private val BackgroundColor = Color(0xFFEAF3EE)

@Composable
fun OtpVerificationScreen(
    viewModel: AuthViewModel,
    onVerificationSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var otpValue by remember {
        mutableStateOf("")
    }

    // TIMER
    var timeLeft by remember {
        mutableStateOf(60)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // COUNTDOWN
    LaunchedEffect(timeLeft) {

        if (timeLeft > 0) {

            delay(1000)
            timeLeft--
        }
    }

    LaunchedEffect(uiState.successMessage) {

        if (uiState.isEmailVerified) {

            onVerificationSuccess()
            viewModel.clearMessages()
        }
    }

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
                top = 24.dp,
                bottom = 40.dp
            )
        ) {

            item {

                Spacer(modifier = Modifier.height(24.dp))

                // LOGO
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "LOGO",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // TITLE
                Text(
                    text = "Verifikasi Email",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LokaMartGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SUBTITLE
                Text(
                    text = "Masukkan 6 digit kode OTP yang telah dikirim ke email Anda.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Kode OTP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // OTP BOXES
                        OtpInputField(
                            otpValue = otpValue,
                            onOtpChange = {
                                otpValue = it
                            }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // TIMER
                        Text(
                            text = if (timeLeft > 0)
                                "00:${timeLeft.toString().padStart(2, '0')}"
                            else
                                "Kode OTP expired",
                            color =
                                if (timeLeft > 0)
                                    LokaMartGreen
                                else
                                    Color.Red,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // VERIFY BUTTON
                        Button(
                            onClick = {
                                viewModel.verifyOtp(otpValue)
                            },
                            enabled =
                                otpValue.length == 6 &&
                                        !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LokaMartGreen,
                                contentColor = Color.White
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
                                    text = "Verifikasi Sekarang",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // RESEND BUTTON
                        TextButton(
                            onClick = {

                                // nanti bisa call resend API
                                viewModel.resendOtp()
                                timeLeft = 60
                            },
                            enabled = timeLeft == 0
                        ) {

                            Text(
                                text = "Kirim ulang kode OTP",
                                color =
                                    if (timeLeft == 0)
                                        LokaMartGreen
                                    else
                                        Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BACK LOGIN
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Sudah punya akun?",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    TextButton(
                        onClick = onBackToLogin
                    ) {

                        Text(
                            text = "Masuk",
                            color = LokaMartGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit
) {

    val focusRequesters = List(6) {
        FocusRequester()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        repeat(6) { index ->

            val char =
                if (index < otpValue.length)
                    otpValue[index].toString()
                else
                    ""

            BasicTextField(
                value = char,
                onValueChange = { value ->

                    if (value.length <= 1 &&
                        value.all { it.isDigit() }
                    ) {

                        val current =
                            otpValue.padEnd(6, ' ').toMutableList()

                        if (value.isNotEmpty()) {
                            current[index] = value[0]
                        } else {
                            current[index] = ' '
                        }

                        onOtpChange(
                            current.joinToString("")
                                .replace(" ", "")
                                .take(6)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(
                        width = 1.5.dp,
                        color =
                            if (char.isNotEmpty())
                                LokaMartGreen
                            else
                                Color(0xFFDADADA),
                        shape = RoundedCornerShape(14.dp)
                    ),
                decorationBox = { innerTextField ->

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        innerTextField()
                    }
                }
            )
        }
    }
}