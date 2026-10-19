package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class SignagePalette(
    val id: String,
    val name: String,
    val bgGradient: Brush,
    val surfaceColor: Color,
    val surfaceBorder: Color,
    val primaryColor: Color,
    val primaryVariant: Color,
    val accentColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val isDark: Boolean = true,
    val badgeBg: Color,
    val tickerBg: Brush,
    val headerBg: Brush
)

object SignageThemePalettes {

    val HighDensity = SignagePalette(
        id = "high_density",
        name = "High Density (M3 Clean)",
        bgGradient = Brush.verticalGradient(
            listOf(Color(0xFFFDF8F8), Color(0xFFF7F2FA), Color(0xFFF3EDF7))
        ),
        surfaceColor = Color(0xFFF3EDF7),
        surfaceBorder = Color(0xFFCAC4D0),
        primaryColor = Color(0xFF6750A4), // M3 Purple
        primaryVariant = Color(0xFF7D5260), // M3 Tertiary
        accentColor = Color(0xFFB3261E), // Crimson Red
        textPrimary = Color(0xFF1C1B1F),
        textSecondary = Color(0xFF49454F),
        isDark = false,
        badgeBg = Color(0xFFEADDFF),
        tickerBg = Brush.horizontalGradient(
            listOf(Color(0xFFB3261E), Color(0xFF9E2119), Color(0xFF8C1D18))
        ),
        headerBg = Brush.horizontalGradient(
            listOf(Color(0xFFF7F2FA), Color(0xFFEDE7F6), Color(0xFFF7F2FA))
        )
    )

    val SchoolModern = SignagePalette(
        id = "school_modern",
        name = "Sekolah Masa Depan (Edu Smart)",
        bgGradient = Brush.verticalGradient(
            listOf(Color(0xFF091428), Color(0xFF0F1E36), Color(0xFF08182B))
        ),
        surfaceColor = Color(0xFF132238),
        surfaceBorder = Color(0xFF1E3A5F),
        primaryColor = Color(0xFF10B981), // Emerald
        primaryVariant = Color(0xFF06B6D4), // Cyan
        accentColor = Color(0xFFF59E0B), // Warm Amber
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        isDark = true,
        badgeBg = Color(0xFF064E3B),
        tickerBg = Brush.horizontalGradient(
            listOf(Color(0xFF064E3B), Color(0xFF065F46), Color(0xFF047857))
        ),
        headerBg = Brush.horizontalGradient(
            listOf(Color(0xFF0A192F), Color(0xFF132B4F), Color(0xFF0A192F))
        )
    )

    val GovNusantara = SignagePalette(
        id = "gov_nusantara",
        name = "Pemerintahan Nusantara (Official)",
        bgGradient = Brush.verticalGradient(
            listOf(Color(0xFF0A1224), Color(0xFF111E3D), Color(0xFF0A1122))
        ),
        surfaceColor = Color(0xFF132140),
        surfaceBorder = Color(0xFF263D6C),
        primaryColor = Color(0xFFF59E0B), // Gold Amber
        primaryVariant = Color(0xFF38BDF8), // Sky Blue
        accentColor = Color(0xFFDC2626), // Crimson
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFFCBD5E1),
        isDark = true,
        badgeBg = Color(0xFF78350F),
        tickerBg = Brush.horizontalGradient(
            listOf(Color(0xFF1E3A8A), Color(0xFF1E40AF), Color(0xFF1D4ED8))
        ),
        headerBg = Brush.horizontalGradient(
            listOf(Color(0xFF0B172E), Color(0xFF1A2E5A), Color(0xFF0B172E))
        )
    )

    val CyberBlue = SignagePalette(
        id = "cyber_blue",
        name = "Cyber Smart City (Futuristic)",
        bgGradient = Brush.verticalGradient(
            listOf(Color(0xFF030712), Color(0xFF0B1528), Color(0xFF050B14))
        ),
        surfaceColor = Color(0xFF0D1B2A),
        surfaceBorder = Color(0xFF00F0FF).copy(alpha = 0.35f),
        primaryColor = Color(0xFF00F0FF), // Neon Cyan
        primaryVariant = Color(0xFF3B82F6), // Electric Blue
        accentColor = Color(0xFFA855F7), // Neon Purple
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF94A3B8),
        isDark = true,
        badgeBg = Color(0xFF083344),
        tickerBg = Brush.horizontalGradient(
            listOf(Color(0xFF082F49), Color(0xFF0C4A6E), Color(0xFF075985))
        ),
        headerBg = Brush.horizontalGradient(
            listOf(Color(0xFF030712), Color(0xFF0F1D38), Color(0xFF030712))
        )
    )

    val MinimalClean = SignagePalette(
        id = "minimal_clean",
        name = "Minimalist Clean (Modern Light)",
        bgGradient = Brush.verticalGradient(
            listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0), Color(0xFFF8FAFC))
        ),
        surfaceColor = Color(0xFFFFFFFF),
        surfaceBorder = Color(0xFFCBD5E1),
        primaryColor = Color(0xFF2563EB),
        primaryVariant = Color(0xFF0284C7),
        accentColor = Color(0xFFEA580C),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF475569),
        isDark = false,
        badgeBg = Color(0xFFDBEAFE),
        tickerBg = Brush.horizontalGradient(
            listOf(Color(0xFF1E293B), Color(0xFF334155), Color(0xFF1E293B))
        ),
        headerBg = Brush.horizontalGradient(
            listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFFFFFFF))
        )
    )

    val SunsetWarm = SignagePalette(
        id = "sunset_warm",
        name = "Sunset Royale (Elegan)",
        bgGradient = Brush.verticalGradient(
            listOf(Color(0xFF1E0A12), Color(0xFF38101E), Color(0xFF1E0A12))
        ),
        surfaceColor = Color(0xFF2D1220),
        surfaceBorder = Color(0xFF5E1E38),
        primaryColor = Color(0xFFF43F5E), // Rose
        primaryVariant = Color(0xFFF59E0B), // Gold
        accentColor = Color(0xFFFB923C), // Orange
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFDA4AF),
        isDark = true,
        badgeBg = Color(0xFF881337),
        tickerBg = Brush.horizontalGradient(
            listOf(Color(0xFF881337), Color(0xFF9F1239), Color(0xFFBE123C))
        ),
        headerBg = Brush.horizontalGradient(
            listOf(Color(0xFF1F0914), Color(0xFF4C1D2E), Color(0xFF1F0914))
        )
    )

    fun getPalette(id: String): SignagePalette {
        return when (id) {
            "high_density" -> HighDensity
            "school_modern" -> SchoolModern
            "gov_nusantara" -> GovNusantara
            "cyber_blue" -> CyberBlue
            "minimal_clean" -> MinimalClean
            "sunset_warm" -> SunsetWarm
            else -> HighDensity
        }
    }
}
