package com.example.lokamart.data.repository

import com.example.lokamart.data.model.Profile
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    // ── Register ──────────────────────────────────────────────
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<UserInfo> {

        return try {

            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password

                this.data = buildJsonObject {
                    put("name", name)
                }
            }

            val user = client.auth.currentUserOrNull()

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(
                    Exception("Registrasi berhasil, tetapi user belum tersedia")
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(mapAuthError(e))
        }
    }

    // ── Login ─────────────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<UserInfo> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = client.auth.currentUserOrNull()
                ?: return Result.failure(Exception("Login gagal, coba lagi"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapAuthError(e))
        }
    }

    // ── Logout ────────────────────────────────────────────────
    suspend fun logout(): Result<Unit> {
        return try {
            client.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Cek sesi aktif ────────────────────────────────────────
    fun getCurrentUser(): UserInfo? {
        return client.auth.currentUserOrNull()
    }

    // ── Ambil profil dari tabel profiles ─────────────────────
    suspend fun getProfile(userId: String): Result<Profile> {
        return try {
            val profile = client.postgrest["profiles"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeSingle<Profile>()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Update nama profil ────────────────────────────────────
    suspend fun updateProfileName(userId: String, name: String): Result<Unit> {
        return try {
            client.postgrest["profiles"]
                .update({ set("name", name) }) {
                    filter { eq("id", userId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Mapping error Supabase ke pesan bahasa Indonesia ─────
    private fun mapAuthError(e: Exception): Exception {
        val msg = e.message?.lowercase() ?: ""
        return when {
            "invalid login credentials" in msg ->
                Exception("Email atau password salah")
            "user already registered" in msg ->
                Exception("Email sudah terdaftar, silakan login")
            "password should be at least" in msg ->
                Exception("Password minimal 6 karakter")
            "unable to validate email" in msg || "invalid email" in msg ->
                Exception("Format email tidak valid")
            "network" in msg || "unable to connect" in msg ->
                Exception("Gagal terhubung ke server, periksa koneksi internet")
            else -> Exception("Terjadi kesalahan: ${e.message}")
        }
    }
}

//    suspend fun register(name: String, email: String, password: String): Result<UserInfo> {
//        return try {
//            client.auth.signUpWith(Email) {
//                this.email = email
//                this.password = password
//                this.data = buildJsonObject { put("name", name) }
//            }
//            val user = client.auth.currentUserOrNull()
//                ?: return Result.failure(Exception("Registrasi gagal, coba lagi"))
//            Result.success(user)
//        } catch (e: Exception) {
//            Result.failure(mapAuthError(e))
//        }
//    }