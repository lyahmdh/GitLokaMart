package com.example.lokamart.ui.screen.auth

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState

// ── Warna LokaMart ────────────────────────────────────────────
val GreenDark = Color(0xFF1A3A2A)
val GreenMedium = Color(0xFF2D5A3D)
val GreenLight = Color(0xFF4CAF50)
val CreamWhite = Color(0xFFF5F0E8)

// ── Data tiap halaman onboarding ──────────────────────────────
data class OnboardingPage(
    val type: PageType,
    val title: String,
    val titleHighlight: String = "",
    val description: String,
    val imageUrl: String = "",
    val badgeLabel: String = "",
    val badgeSubLabel: String = ""
)

enum class PageType { SPLASH, ONBOARDING_IMAGE, ONBOARDING_FEATURE, ONBOARDING_MISSION }

val onboardingPages = listOf(
    OnboardingPage(
        type = PageType.SPLASH,
        title = "LokaMart",
        description = "Menghubungkan UMKM Lokal\ndengan Komunitas"
    ),
    OnboardingPage(
        type = PageType.ONBOARDING_IMAGE,
        title = "Temukan Produk ",
        titleHighlight = "Lokal Unggulan",
        description = "Jelajahi berbagai produk kerajinan dan kuliner khas dari UMKM terbaik di sekitar Anda.",
        imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600",
        badgeLabel = "UMKM TERKURASI",
        badgeSubLabel = "Kualitas Premium Lokal"
    ),
    OnboardingPage(
        type = PageType.ONBOARDING_FEATURE,
        title = "Belanja Aman & Terpercaya",
        description = "Gunakan sistem On Hold selama 14 hari untuk memastikan ketersediaan produk sebelum Anda membayar."
    ),
    OnboardingPage(
        type = PageType.ONBOARDING_MISSION,
        title = "Dukung Ekonomi Lokal",
        description = "Setiap transaksi Anda membantu pertumbuhan usaha mikro dan memperkuat komunitas ekonomi lokal kita.",
        imageUrl = "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=600",
        badgeLabel = "MISI KAMI",
        badgeSubLabel = "Memberdayakan 1000+ UMKM Lokal"
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(
    onFinish: () -> Unit
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { onboardingPages.size }
    )

    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->

        when (onboardingPages[page].type) {

            PageType.SPLASH -> {
                SplashPage(
                    onStart = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = 1,
                                animationSpec = tween(500)
                            )
                        }
                    }
                )
            }

            PageType.ONBOARDING_IMAGE -> {
                OnboardingImagePage(
                    data = onboardingPages[page],
                    pagerState = pagerState,
                    currentPage = page,
                    onNext = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = page + 1,
                                animationSpec = tween(500)
                            )
                        }
                    }
                )
            }

            PageType.ONBOARDING_FEATURE -> {
                OnboardingFeaturePage(
                    data = onboardingPages[page],
                    pagerState = pagerState,
                    currentPage = page,
                    onNext = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = page + 1,
                                animationSpec = tween(500)
                            )
                        }
                    }
                )
            }

            PageType.ONBOARDING_MISSION -> {
                OnboardingMissionPage(
                    data = onboardingPages[page],
                    pagerState = pagerState,
                    currentPage = page,
                    onFinish = onFinish
                )
            }
        }
    }
}

// ── Halaman 1: Splash utama (hijau gelap, logo, tombol mulai) ─
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashPage(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon toko
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🏪", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LokaMart",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Menghubungkan UMKM Lokal\ndengan Komunitas",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        // Tombol Mulai Sekarang di bawah
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp, Color.White.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = "Mulai Sekarang  →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Halaman 2: Onboarding dengan gambar produk + badge ────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingImagePage(
    data: OnboardingPage,
    pagerState: PagerState,
    currentPage: Int,
    onNext: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F5))
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        // CARD IMAGE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .clip(RoundedCornerShape(32.dp))
        ) {

            AsyncImage(
                model = data.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(18.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GreenDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏪")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = data.badgeLabel,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = data.badgeSubLabel,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // CONTENT BOTTOM
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = data.title + data.titleHighlight,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = data.description,
                fontSize = 16.sp,
                lineHeight = 28.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(34.dp))

            DotsIndicator(
                totalDots = 3,
                selectedIndex = currentPage - 1
            )

            Spacer(modifier = Modifier.height(34.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenDark
                )
            ) {

                Text(
                    text = "Lanjut  →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Halaman 3: Onboarding fitur keamanan (background abu) ─────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingFeaturePage(
    data: OnboardingPage,
    pagerState: PagerState,
    currentPage: Int,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // Card preview fitur On Hold
        Card(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛡️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TRANSAKSI AMAN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AKTIF",
                            fontSize = 10.sp,
                            color = GreenMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mockup produk
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFD4E8D4))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFCCCCCC))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFDDDDDD))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Badge 14 hari
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛡️", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "14 Hari",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                        Text(
                            text = "Sistem On Hold",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Badge dana terlindungi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DANA TERLINDUNGI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Teks bawah
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.description,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            DotsIndicator(
                totalDots = 3,
                selectedIndex = currentPage - 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GreenDark)
            ) {
                Text(
                    text = "Lanjut  →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Halaman 4: Misi (gambar penjual + badge misi) ─────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingMissionPage(
    data: OnboardingPage,
    pagerState: PagerState,
    currentPage: Int,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F0E8)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f))

        // Card gambar penjual
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8FB89F)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box {
                AsyncImage(
                    model = data.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                // Badge misi di bawah gambar dalam card
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌱", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = data.badgeLabel,
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = data.badgeSubLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenDark
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.3f))

        // Teks bawah
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.description,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            DotsIndicator(
                totalDots = 3,
                selectedIndex = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
            ) {
                Text(
                    text = "Mulai Sekarang  →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Dots indicator (titik halaman) ────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->

            val isSelected = index == selectedIndex

            val width by animateDpAsState(
                targetValue = if (isSelected) 26.dp else 8.dp,
                animationSpec = tween(
                    durationMillis = 300
                ),
                label = ""
            )

            val color by animateColorAsState(
                targetValue =
                    if (isSelected) GreenDark
                    else Color(0xFFD0D0D0),
                animationSpec = tween(300),
                label = ""
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}