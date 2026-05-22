package com.example.lokamart.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OtpApiService {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private val baseUrl =
        "https://gknjnodksyrhmaxzkdrk.supabase.co/functions/v1"

    private val apiKey =
        "ISI_SUPABASE_ANON_KEY_KAMU"

    suspend fun sendOtp(email: String): Result<String> {

        return try {

            val response: SendOtpResponse =
                client.post("$baseUrl/send-otp") {

                    contentType(ContentType.Application.Json)

                    header("apikey", apiKey)

                    setBody(
                        SendOtpRequest(email)
                    )
                }.body()

            if (response.success) {

                Result.success(response.message)

            } else {

                Result.failure(
                    Exception(response.error ?: "Gagal kirim OTP")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun verifyOtp(
        email: String,
        otp: String
    ): Result<String> {

        return try {

            val response: VerifyOtpResponse =
                client.post("$baseUrl/verify-otp") {

                    contentType(ContentType.Application.Json)

                    header("apikey", apiKey)

                    setBody(
                        VerifyOtpRequest(
                            email = email,
                            otp = otp
                        )
                    )
                }.body()

            if (response.success) {

                Result.success(response.message)

            } else {

                Result.failure(
                    Exception(response.error ?: "OTP salah")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}

@Serializable
data class SendOtpRequest(
    val email: String
)

@Serializable
data class SendOtpResponse(
    val success: Boolean,
    val message: String,
    val error: String? = null
)

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

@Serializable
data class VerifyOtpResponse(
    val success: Boolean,
    val message: String,
    val error: String? = null
)