package com.example.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

data class AppItem(
    val id: String,
    val name: String,
    val packageName: String? = null,
    val iconName: String,
    val gradientColors: List<Color>,
    val isDock: Boolean = false,
    val isBuiltIn: Boolean = true
)

enum class ScreenState {
    LOCK_SCREEN,
    HOME_SCREEN,
    SETTINGS_SCREEN
}

enum class ClockStyle(
    val id: String,
    val displayName: String,
    val fontFamily: FontFamily,
    val fontWeight: FontWeight,
    val letterSpacing: Float = 0f
) {
    CLASSIC_BOLD("classic_bold", "Bold", FontFamily.Default, FontWeight.Bold, -2f),
    ROUNDED("rounded", "Rounded", FontFamily.SansSerif, FontWeight.SemiBold, 0f),
    SERIF("serif", "Serif", FontFamily.Serif, FontWeight.Normal, 1f),
    MONOSPACE("monospace", "Digital", FontFamily.Monospace, FontWeight.Medium, 2f),
    SLIM("slim", "Ultralight", FontFamily.Default, FontWeight.Light, -1f)
}

data class WallpaperOption(
    val id: String,
    val title: String,
    val drawableRes: Int? = null,
    val gradientColors: List<Color>? = null,
    val isDark: Boolean = true
)

enum class IslandMode {
    INACTIVE,
    MUSIC,
    TIMER,
    TORCH,
    BATTERY
}

data class DynamicIslandData(
    val mode: IslandMode = IslandMode.MUSIC,
    val isExpanded: Boolean = false,
    val trackTitle: String = "Starlight Symphony",
    val artistName: String = "Nova Sound",
    val isPlaying: Boolean = true,
    val playbackProgress: Float = 0.42f,
    val timerRemainingSeconds: Int = 185,
    val isTimerActive: Boolean = true,
    val batteryPercent: Int = 88,
    val isTorchActive: Boolean = false
)

enum class WidgetType {
    WEATHER,
    CLOCK_CALENDAR,
    BATTERY
}
