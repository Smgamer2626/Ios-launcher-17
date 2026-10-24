package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenState
import com.example.ui.screens.ControlCenter
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LauncherViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                LauncherRootApp(activity = this, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LauncherRootApp(
    activity: MainActivity,
    viewModel: LauncherViewModel
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val isControlCenterOpen by viewModel.isControlCenterOpen.collectAsStateWithLifecycle()
    val dynamicIslandData by viewModel.dynamicIsland.collectAsStateWithLifecycle()
    val selectedWallpaper by viewModel.selectedWallpaper.collectAsStateWithLifecycle()
    val clockStyle by viewModel.clockStyle.collectAsStateWithLifecycle()
    val clockColor by viewModel.clockColor.collectAsStateWithLifecycle()
    val isTorchOn by viewModel.isTorchOn.collectAsStateWithLifecycle()
    val volumeLevel by viewModel.volumeLevel.collectAsStateWithLifecycle()
    val brightnessLevel by viewModel.brightnessLevel.collectAsStateWithLifecycle()
    val isWifiOn by viewModel.isWifiOn.collectAsStateWithLifecycle()
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsStateWithLifecycle()
    val isAirplaneOn by viewModel.isAirplaneOn.collectAsStateWithLifecycle()
    val isCellularOn by viewModel.isCellularOn.collectAsStateWithLifecycle()
    val isDndOn by viewModel.isDndOn.collectAsStateWithLifecycle()
    val isLowPowerOn by viewModel.isLowPowerOn.collectAsStateWithLifecycle()
    val currentTimeStr by viewModel.currentTimeStr.collectAsStateWithLifecycle()
    val currentDateStr by viewModel.currentDateStr.collectAsStateWithLifecycle()
    val isEditMode by viewModel.isEditMode.collectAsStateWithLifecycle()
    val dockApps by viewModel.dockApps.collectAsStateWithLifecycle()
    val homeApps by viewModel.homeGridApps.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isHapticEnabled.collectAsStateWithLifecycle()

    // Back handling
    BackHandler(enabled = isControlCenterOpen || isEditMode || screenState != ScreenState.LOCK_SCREEN) {
        when {
            isControlCenterOpen -> viewModel.closeControlCenter()
            isEditMode -> viewModel.toggleEditMode()
            screenState == ScreenState.SETTINGS_SCREEN -> viewModel.navigateTo(ScreenState.HOME_SCREEN)
            screenState == ScreenState.HOME_SCREEN -> viewModel.lockScreen()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Universal Wallpaper Layer
        if (selectedWallpaper.drawableRes != null) {
            Image(
                painter = painterResource(id = selectedWallpaper.drawableRes!!),
                contentDescription = "Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (selectedWallpaper.gradientColors != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(selectedWallpaper.gradientColors!!))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        // Screen State Transitions
        Crossfade(targetState = screenState, label = "screen_crossfade") { state ->
            when (state) {
                ScreenState.LOCK_SCREEN -> {
                    LockScreen(
                        timeStr = currentTimeStr,
                        dateStr = currentDateStr,
                        clockStyle = clockStyle,
                        clockColor = clockColor,
                        isTorchOn = isTorchOn,
                        dynamicIslandData = dynamicIslandData,
                        onToggleTorch = { viewModel.toggleTorch() },
                        onLaunchCamera = { viewModel.systemManager.launchCamera() },
                        onUnlock = { viewModel.unlockToHome() },
                        onOpenControlCenter = { viewModel.toggleControlCenter() },
                        onToggleDynamicIsland = { viewModel.toggleDynamicIslandExpanded() },
                        onTogglePlay = { viewModel.toggleMusicPlayback() },
                        onNextTrack = { viewModel.nextMusicTrack() },
                        onPrevTrack = { viewModel.prevMusicTrack() },
                        onToggleTimer = { viewModel.toggleTimerActive() },
                        onResetTimer = { viewModel.resetTimer() }
                    )
                }

                ScreenState.HOME_SCREEN -> {
                    HomeScreen(
                        apps = homeApps,
                        dockApps = dockApps,
                        dynamicIslandData = dynamicIslandData,
                        dateStr = currentDateStr,
                        isEditMode = isEditMode,
                        onAppClick = { app -> viewModel.launchApp(app) },
                        onAppLongClick = { _ -> viewModel.toggleEditMode() },
                        onExitEditMode = { viewModel.toggleEditMode() },
                        onOpenControlCenter = { viewModel.toggleControlCenter() },
                        onLockScreen = { viewModel.lockScreen() },
                        onOpenSettings = { viewModel.navigateTo(ScreenState.SETTINGS_SCREEN) },
                        onToggleDynamicIsland = { viewModel.toggleDynamicIslandExpanded() },
                        onTogglePlay = { viewModel.toggleMusicPlayback() },
                        onNextTrack = { viewModel.nextMusicTrack() },
                        onPrevTrack = { viewModel.prevMusicTrack() },
                        onToggleTimer = { viewModel.toggleTimerActive() },
                        onResetTimer = { viewModel.resetTimer() },
                        onToggleTorch = { viewModel.toggleTorch() }
                    )
                }

                ScreenState.SETTINGS_SCREEN -> {
                    SettingsScreen(
                        selectedWallpaper = selectedWallpaper,
                        wallpapers = viewModel.availableWallpapers,
                        onSelectWallpaper = { wp -> viewModel.setWallpaper(wp) },
                        clockStyle = clockStyle,
                        onSelectClockStyle = { cs -> viewModel.setClockStyle(cs) },
                        clockColor = clockColor,
                        onSelectClockColor = { cc -> viewModel.setClockColor(cc) },
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { dark -> viewModel.setDarkTheme(dark) },
                        isHapticEnabled = isHapticEnabled,
                        onToggleHaptic = { haptic -> viewModel.setHapticEnabled(haptic) },
                        onSetDefaultLauncher = { viewModel.systemManager.openDefaultHomeSettings() },
                        onResetDefaults = { viewModel.resetToDefaults() },
                        onBack = { viewModel.navigateTo(ScreenState.HOME_SCREEN) }
                    )
                }
            }
        }

        // Control Center Layer Overlay (can be pulled down over lockscreen or home screen)
        ControlCenter(
            isOpen = isControlCenterOpen,
            onDismiss = { viewModel.closeControlCenter() },
            isWifiOn = isWifiOn,
            onToggleWifi = { viewModel.toggleWifi() },
            isBluetoothOn = isBluetoothOn,
            onToggleBluetooth = { viewModel.toggleBluetooth() },
            isAirplaneOn = isAirplaneOn,
            onToggleAirplane = { viewModel.toggleAirplane() },
            isCellularOn = isCellularOn,
            onToggleCellular = { viewModel.toggleCellular() },
            brightness = brightnessLevel,
            onBrightnessChange = { b -> viewModel.setBrightness(activity, b) },
            volume = volumeLevel,
            onVolumeChange = { v -> viewModel.setVolume(v) },
            isTorchOn = isTorchOn,
            onToggleTorch = { viewModel.toggleTorch() },
            isDndOn = isDndOn,
            onToggleDnd = { viewModel.toggleDnd() },
            isLowPowerOn = isLowPowerOn,
            onToggleLowPower = { viewModel.toggleLowPower() },
            dynamicIslandData = dynamicIslandData,
            onTogglePlay = { viewModel.toggleMusicPlayback() },
            onLaunchCamera = { viewModel.systemManager.launchCamera() },
            onOpenTimer = {
                viewModel.setIslandMode(com.example.model.IslandMode.TIMER)
                viewModel.closeControlCenter()
                viewModel.toggleDynamicIslandExpanded()
            },
            onOpenSettings = {
                viewModel.closeControlCenter()
                viewModel.navigateTo(ScreenState.SETTINGS_SCREEN)
            }
        )
    }
}
