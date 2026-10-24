package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.model.AppItem
import com.example.model.ClockStyle
import com.example.model.DynamicIslandData
import com.example.model.IslandMode
import com.example.model.ScreenState
import com.example.model.WallpaperOption
import com.example.system.AndroidSystemManager
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleIndigo
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.ApplePink
import com.example.ui.theme.ApplePurple
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    val systemManager = AndroidSystemManager(application)

    // Current Screen
    private val _screenState = MutableStateFlow(ScreenState.LOCK_SCREEN)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    // Control Center
    private val _isControlCenterOpen = MutableStateFlow(false)
    val isControlCenterOpen: StateFlow<Boolean> = _isControlCenterOpen.asStateFlow()

    // Dynamic Island
    private val _dynamicIsland = MutableStateFlow(DynamicIslandData())
    val dynamicIsland: StateFlow<DynamicIslandData> = _dynamicIsland.asStateFlow()

    // Appearance & Customization
    private val _clockStyle = MutableStateFlow(ClockStyle.CLASSIC_BOLD)
    val clockStyle: StateFlow<ClockStyle> = _clockStyle.asStateFlow()

    private val _clockColor = MutableStateFlow(Color.White)
    val clockColor: StateFlow<Color> = _clockColor.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isHapticEnabled = MutableStateFlow(true)
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    // iOS HUD Toast
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()
    private var toastJob: kotlinx.coroutines.Job? = null

    fun showToast(msg: String) {
        toastJob?.cancel()
        _toastMessage.value = msg
        toastJob = viewModelScope.launch {
            delay(1800)
            _toastMessage.value = null
        }
    }

    // Wallpapers
    val availableWallpapers = listOf(
        WallpaperOption(
            id = "aurora",
            title = "iOS 18 Aurora",
            drawableRes = R.drawable.ios_wallpaper_default,
            isDark = true
        ),
        WallpaperOption(
            id = "deep_ocean",
            title = "Deep Ocean",
            gradientColors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
            isDark = true
        ),
        WallpaperOption(
            id = "sunset_glow",
            title = "Sunset Bloom",
            gradientColors = listOf(Color(0xFFFF512F), Color(0xFFDD2476), Color(0xFF4A00E0)),
            isDark = true
        ),
        WallpaperOption(
            id = "midnight_cyber",
            title = "Midnight Violet",
            gradientColors = listOf(Color(0xFF0D0D14), Color(0xFF1E1136), Color(0xFF4B1248)),
            isDark = true
        ),
        WallpaperOption(
            id = "minimal_slate",
            title = "Graphite Glass",
            gradientColors = listOf(Color(0xFF1C1C1E), Color(0xFF2C2C2E), Color(0xFF121214)),
            isDark = true
        ),
        WallpaperOption(
            id = "pastel_daybreak",
            title = "Daybreak Gradient",
            gradientColors = listOf(Color(0xFF8EC5FC), Color(0xFFE0C3FC)),
            isDark = false
        )
    )

    private val _selectedWallpaper = MutableStateFlow(availableWallpapers[0])
    val selectedWallpaper: StateFlow<WallpaperOption> = _selectedWallpaper.asStateFlow()

    // Control Center toggles & levels
    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    private val _volumeLevel = MutableStateFlow(systemManager.getVolumeFraction())
    val volumeLevel: StateFlow<Float> = _volumeLevel.asStateFlow()

    private val _brightnessLevel = MutableStateFlow(0.75f)
    val brightnessLevel: StateFlow<Float> = _brightnessLevel.asStateFlow()

    private val _isWifiOn = MutableStateFlow(true)
    val isWifiOn: StateFlow<Boolean> = _isWifiOn.asStateFlow()

    private val _isBluetoothOn = MutableStateFlow(true)
    val isBluetoothOn: StateFlow<Boolean> = _isBluetoothOn.asStateFlow()

    private val _isAirplaneOn = MutableStateFlow(false)
    val isAirplaneOn: StateFlow<Boolean> = _isAirplaneOn.asStateFlow()

    private val _isCellularOn = MutableStateFlow(true)
    val isCellularOn: StateFlow<Boolean> = _isCellularOn.asStateFlow()

    private val _isDndOn = MutableStateFlow(false)
    val isDndOn: StateFlow<Boolean> = _isDndOn.asStateFlow()

    private val _isLowPowerOn = MutableStateFlow(false)
    val isLowPowerOn: StateFlow<Boolean> = _isLowPowerOn.asStateFlow()

    // Date and Time live strings
    private val _currentTimeStr = MutableStateFlow("")
    val currentTimeStr: StateFlow<String> = _currentTimeStr.asStateFlow()

    private val _currentDateStr = MutableStateFlow("")
    val currentDateStr: StateFlow<String> = _currentDateStr.asStateFlow()

    // Home Screen apps & Edit/Jiggle mode
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _dockApps = MutableStateFlow<List<AppItem>>(emptyList())
    val dockApps: StateFlow<List<AppItem>> = _dockApps.asStateFlow()

    private val _homeGridApps = MutableStateFlow<List<AppItem>>(emptyList())
    val homeGridApps: StateFlow<List<AppItem>> = _homeGridApps.asStateFlow()

    init {
        initDefaultApps()
        startClockTicker()
        startDynamicIslandTicker()
    }

    private fun initDefaultApps() {
        val dock = listOf(
            AppItem("dock_phone", "Phone", null, "phone", listOf(Color(0xFF34C759), Color(0xFF30D158)), isDock = true),
            AppItem("dock_safari", "Safari", null, "safari", listOf(Color(0xFF007AFF), Color(0xFF5AC8FA)), isDock = true),
            AppItem("dock_messages", "Messages", null, "messages", listOf(Color(0xFF34C759), Color(0xFF28CD41)), isDock = true),
            AppItem("dock_camera", "Camera", null, "camera", listOf(Color(0xFF8E8E93), Color(0xFF636366)), isDock = true)
        )
        _dockApps.value = dock

        val initialGrid = mutableListOf(
            AppItem("app_photos", "Photos", null, "photos", listOf(Color(0xFFFF9500), Color(0xFFFF2D55))),
            AppItem("app_weather", "Weather", null, "weather", listOf(Color(0xFF007AFF), Color(0xFF5AC8FA))),
            AppItem("app_clock", "Clock", null, "clock", listOf(Color(0xFF1C1C1E), Color(0xFF2C2C2E))),
            AppItem("app_calculator", "Calculator", null, "calculator", listOf(Color(0xFFFF9500), Color(0xFFFF9F0A))),
            AppItem("app_notes", "Notes", null, "notes", listOf(Color(0xFFFFCC00), Color(0xFFFFD60A))),
            AppItem("app_music", "Music", null, "music", listOf(Color(0xFFFF2D55), Color(0xFFFF375F))),
            AppItem("app_maps", "Maps", null, "maps", listOf(Color(0xFF34C759), Color(0xFF007AFF))),
            AppItem("app_calendar", "Calendar", null, "calendar", listOf(Color(0xFFFF3B30), Color(0xFFFF453A))),
            AppItem("app_settings", "Settings", null, "settings", listOf(Color(0xFF8E8E93), Color(0xFF636366))),
            AppItem("app_files", "Files", null, "files", listOf(Color(0xFF007AFF), Color(0xFF5856D6))),
            AppItem("app_health", "Health", null, "health", listOf(Color(0xFFFF2D55), Color(0xFFFF375F))),
            AppItem("app_podcasts", "Podcasts", null, "podcasts", listOf(Color(0xFFAF52DE), Color(0xFFBF5AF2)))
        )

        // Query real installed Android apps to include on subsequent slots!
        viewModelScope.launch {
            val installed = systemManager.getInstalledApps()
            val colorsPalette = listOf(
                listOf(AppleBlue, AppleTeal),
                listOf(ApplePurple, AppleIndigo),
                listOf(AppleOrange, AppleRed),
                listOf(AppleGreen, AppleTeal),
                listOf(ApplePink, ApplePurple)
            )
            installed.take(8).forEachIndexed { idx, pair ->
                val (label, pkg) = pair
                initialGrid.add(
                    AppItem(
                        id = "pkg_$pkg",
                        name = label,
                        packageName = pkg,
                        iconName = "android_app",
                        gradientColors = colorsPalette[idx % colorsPalette.size],
                        isBuiltIn = false
                    )
                )
            }
            _homeGridApps.value = initialGrid
        }
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            while (true) {
                val now = Date()
                _currentTimeStr.value = timeFormat.format(now)
                _currentDateStr.value = dateFormat.format(now)
                delay(1000)
            }
        }
    }

    private fun startDynamicIslandTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _dynamicIsland.value
                if (current.mode == IslandMode.TIMER && current.isTimerActive && current.timerRemainingSeconds > 0) {
                    _dynamicIsland.value = current.copy(
                        timerRemainingSeconds = current.timerRemainingSeconds - 1
                    )
                } else if (current.mode == IslandMode.MUSIC && current.isPlaying) {
                    val nextProg = (current.playbackProgress + 0.01f)
                    _dynamicIsland.value = current.copy(
                        playbackProgress = if (nextProg > 1.0f) 0f else nextProg
                    )
                }
            }
        }
    }

    fun navigateTo(state: ScreenState) {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _screenState.value = state
        _isControlCenterOpen.value = false
    }

    fun unlockToHome() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback(35)
        _screenState.value = ScreenState.HOME_SCREEN
    }

    fun lockScreen() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback(25)
        _screenState.value = ScreenState.LOCK_SCREEN
        _isControlCenterOpen.value = false
    }

    fun toggleControlCenter() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback(20)
        _isControlCenterOpen.value = !_isControlCenterOpen.value
    }

    fun closeControlCenter() {
        _isControlCenterOpen.value = false
    }

    fun toggleTorch() {
        val next = !_isTorchOn.value
        val success = systemManager.setTorch(next)
        if (success) {
            _isTorchOn.value = next
            if (next) {
                setIslandMode(IslandMode.TORCH)
                showToast("Flashlight Activated")
            } else {
                if (_dynamicIsland.value.mode == IslandMode.TORCH) {
                    setIslandMode(IslandMode.MUSIC)
                }
                showToast("Flashlight Turned Off")
            }
        }
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
    }

    fun setVolume(level: Float) {
        _volumeLevel.value = level
        systemManager.setVolumeFraction(level)
    }

    fun setBrightness(activity: Activity?, level: Float) {
        _brightnessLevel.value = level
        systemManager.setWindowBrightness(activity, level)
    }

    fun toggleWifi() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _isWifiOn.value = !_isWifiOn.value
        showToast("Opening Wi-Fi Settings...")
        systemManager.openWifiSettings()
    }

    fun toggleBluetooth() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _isBluetoothOn.value = !_isBluetoothOn.value
        showToast("Opening Bluetooth Settings...")
        systemManager.openBluetoothSettings()
    }

    fun toggleAirplane() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _isAirplaneOn.value = !_isAirplaneOn.value
        showToast("Opening Airplane Mode Settings...")
        systemManager.openAirplaneSettings()
    }

    fun toggleCellular() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _isCellularOn.value = !_isCellularOn.value
        showToast("Opening Cellular Settings...")
        systemManager.openDataSettings()
    }

    fun toggleDnd() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _isDndOn.value = !_isDndOn.value
        showToast("Focus / Do Not Disturb")
        systemManager.openDndSettings()
    }

    fun toggleLowPower() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        val next = !_isLowPowerOn.value
        _isLowPowerOn.value = next
        showToast(if (next) "Low Power Mode Enabled" else "Low Power Mode Disabled")
    }

    fun toggleDynamicIslandExpanded() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback(15)
        _dynamicIsland.value = _dynamicIsland.value.copy(
            isExpanded = !_dynamicIsland.value.isExpanded
        )
    }

    fun setIslandMode(mode: IslandMode) {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _dynamicIsland.value = _dynamicIsland.value.copy(mode = mode)
    }

    fun toggleMusicPlayback() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _dynamicIsland.value = _dynamicIsland.value.copy(
            isPlaying = !_dynamicIsland.value.isPlaying
        )
    }

    fun nextMusicTrack() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        val tracks = listOf(
            "Midnight City" to "Nova Synth",
            "Golden Hour" to "Skyline",
            "Starlight Symphony" to "Nova Sound",
            "Solar Flare" to "Astral Echo"
        )
        val currentIdx = tracks.indexOfFirst { it.first == _dynamicIsland.value.trackTitle }
        val next = tracks[(currentIdx + 1).coerceAtLeast(0) % tracks.size]
        _dynamicIsland.value = _dynamicIsland.value.copy(
            trackTitle = next.first,
            artistName = next.second,
            playbackProgress = 0f
        )
    }

    fun prevMusicTrack() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _dynamicIsland.value = _dynamicIsland.value.copy(playbackProgress = 0f)
    }

    fun toggleTimerActive() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _dynamicIsland.value = _dynamicIsland.value.copy(
            isTimerActive = !_dynamicIsland.value.isTimerActive
        )
    }

    fun resetTimer() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _dynamicIsland.value = _dynamicIsland.value.copy(
            timerRemainingSeconds = 180,
            isTimerActive = true
        )
    }

    fun setClockStyle(style: ClockStyle) {
        _clockStyle.value = style
    }

    fun setClockColor(color: Color) {
        _clockColor.value = color
    }

    fun setWallpaper(option: WallpaperOption) {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        _selectedWallpaper.value = option
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun setHapticEnabled(enabled: Boolean) {
        _isHapticEnabled.value = enabled
    }

    fun toggleEditMode() {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback(40)
        _isEditMode.value = !_isEditMode.value
    }

    fun launchApp(app: AppItem) {
        if (_isHapticEnabled.value) systemManager.triggerHapticFeedback()
        if (_isEditMode.value) {
            _isEditMode.value = false
            return
        }

        if (app.packageName != null) {
            val launched = systemManager.launchPackage(app.packageName)
            showToast(if (launched) "Opening ${app.name}..." else "Failed to open ${app.name}")
            return
        }

        when (app.id) {
            "dock_phone" -> {
                showToast("Opening Phone...")
                systemManager.launchDialer()
            }
            "dock_messages" -> {
                showToast("Opening Messages...")
                systemManager.launchMessages()
            }
            "dock_safari" -> {
                showToast("Opening Safari...")
                systemManager.launchBrowser()
            }
            "dock_camera", "app_camera" -> {
                showToast("Opening Camera...")
                systemManager.launchCamera()
            }
            "app_photos" -> {
                showToast("Opening Photos Gallery...")
                systemManager.launchPhotos()
            }
            "app_calculator" -> {
                showToast("Opening Calculator...")
                systemManager.launchCalculator()
            }
            "app_notes" -> {
                showToast("Opening Notes...")
                systemManager.launchNotes()
            }
            "app_settings" -> {
                showToast("Opening Settings...")
                navigateTo(ScreenState.SETTINGS_SCREEN)
            }
            "app_weather" -> {
                showToast("Cupertino • 72° Sunny")
                toggleDynamicIslandExpanded()
            }
            "app_clock" -> {
                setIslandMode(IslandMode.TIMER)
                _dynamicIsland.value = _dynamicIsland.value.copy(isExpanded = true)
                showToast("Countdown Timer Active")
            }
            "app_music" -> {
                setIslandMode(IslandMode.MUSIC)
                _dynamicIsland.value = _dynamicIsland.value.copy(isExpanded = true)
                showToast("Now Playing: ${_dynamicIsland.value.trackTitle}")
            }
            "app_calendar" -> {
                showToast("Opening Calendar...")
                navigateTo(ScreenState.SETTINGS_SCREEN)
            }
            "app_maps" -> {
                showToast("Launching Navigation...")
                systemManager.launchBrowser()
            }
            else -> {
                showToast("Selected ${app.name}")
                toggleDynamicIslandExpanded()
            }
        }
    }

    fun resetToDefaults() {
        _clockStyle.value = ClockStyle.CLASSIC_BOLD
        _clockColor.value = Color.White
        _selectedWallpaper.value = availableWallpapers[0]
        _isDarkTheme.value = true
        _isHapticEnabled.value = true
        _isEditMode.value = false
    }
}
