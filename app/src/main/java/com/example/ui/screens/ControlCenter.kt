package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DynamicIslandData
import com.example.ui.components.ControlCenterSlider
import com.example.ui.components.GlassCard
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.ApplePurple
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleYellow

@Composable
fun ControlCenter(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    isWifiOn: Boolean,
    onToggleWifi: () -> Unit,
    isBluetoothOn: Boolean,
    onToggleBluetooth: () -> Unit,
    isAirplaneOn: Boolean,
    onToggleAirplane: () -> Unit,
    isCellularOn: Boolean,
    onToggleCellular: () -> Unit,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    isTorchOn: Boolean,
    onToggleTorch: () -> Unit,
    isDndOn: Boolean,
    onToggleDnd: () -> Unit,
    isLowPowerOn: Boolean,
    onToggleLowPower: () -> Unit,
    dynamicIslandData: DynamicIslandData,
    onTogglePlay: () -> Unit,
    onLaunchCamera: () -> Unit,
    onOpenTimer: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 3 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 3 })
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("control_center_overlay")
                .background(Color(0xCC000000))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Dismiss Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = AppleGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "88% • Nova Control",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x35FFFFFF))
                            .clickable { onDismiss() }
                            .testTag("control_center_close_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Row 1: Connectivity Pod (2x2) and Now Playing Pod
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Connectivity Pod
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(154.dp),
                        shape = RoundedCornerShape(26.dp),
                        backgroundColor = Color(0x352C2C2E)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ConnectivityButton(
                                    icon = Icons.Default.AirplanemodeActive,
                                    isActive = isAirplaneOn,
                                    activeColor = AppleOrange,
                                    onClick = onToggleAirplane,
                                    testTag = "cc_airplane_btn"
                                )
                                ConnectivityButton(
                                    icon = Icons.Default.SignalCellularAlt,
                                    isActive = isCellularOn,
                                    activeColor = AppleGreen,
                                    onClick = onToggleCellular,
                                    testTag = "cc_cellular_btn"
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ConnectivityButton(
                                    icon = Icons.Default.Wifi,
                                    isActive = isWifiOn,
                                    activeColor = AppleBlue,
                                    onClick = onToggleWifi,
                                    testTag = "cc_wifi_btn"
                                )
                                ConnectivityButton(
                                    icon = Icons.Default.Bluetooth,
                                    isActive = isBluetoothOn,
                                    activeColor = AppleBlue,
                                    onClick = onToggleBluetooth,
                                    testTag = "cc_bluetooth_btn"
                                )
                            }
                        }
                    }

                    // Now Playing Pod
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(154.dp),
                        shape = RoundedCornerShape(26.dp),
                        backgroundColor = Color(0x352C2C2E)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dynamicIslandData.trackTitle,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = dynamicIslandData.artistName,
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = AppleRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { onTogglePlay() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (dynamicIslandData.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Text(
                                text = "AirPlay • iPhone Speaker",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Row 2: Sliders and Focus Pod
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left 2x1 pod: Focus / DND and Orientation Lock
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Focus / DND Tile
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp),
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = if (isDndOn) ApplePurple.copy(alpha = 0.4f) else Color(0x352C2C2E),
                            onClick = onToggleDnd
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoNotDisturb,
                                    contentDescription = "Do Not Disturb",
                                    tint = if (isDndOn) ApplePurple else Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Focus",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (isDndOn) "On" else "Off",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Screen Mirroring / Low Power Tile
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp),
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = if (isLowPowerOn) AppleYellow.copy(alpha = 0.4f) else Color(0x352C2C2E),
                            onClick = onToggleLowPower
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatterySaver,
                                    contentDescription = "Low Power Mode",
                                    tint = if (isLowPowerOn) AppleYellow else Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Low Power",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (isLowPowerOn) "Active" else "Off",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // Brightness Slider
                    ControlCenterSlider(
                        value = brightness,
                        onValueChange = onBrightnessChange,
                        icon = Icons.Default.BrightnessHigh,
                        contentDescription = "Brightness",
                        testTag = "cc_brightness_slider"
                    )

                    // Volume Slider
                    val volumeIcon = when {
                        volume <= 0.05f -> Icons.Default.VolumeMute
                        volume < 0.5f -> Icons.Default.VolumeDown
                        else -> Icons.Default.VolumeUp
                    }
                    ControlCenterSlider(
                        value = volume,
                        onValueChange = onVolumeChange,
                        icon = volumeIcon,
                        contentDescription = "Volume",
                        testTag = "cc_volume_slider"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Row 3: Action Buttons (Torch, Timer, Camera, Settings)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionButton(
                        icon = Icons.Default.FlashlightOn,
                        label = "Torch",
                        isActive = isTorchOn,
                        activeColor = AppleYellow,
                        onClick = onToggleTorch,
                        testTag = "cc_quick_torch"
                    )
                    QuickActionButton(
                        icon = Icons.Default.Timer,
                        label = "Timer",
                        isActive = false,
                        activeColor = AppleOrange,
                        onClick = onOpenTimer,
                        testTag = "cc_quick_timer"
                    )
                    QuickActionButton(
                        icon = Icons.Default.CameraAlt,
                        label = "Camera",
                        isActive = false,
                        activeColor = Color.White,
                        onClick = onLaunchCamera,
                        testTag = "cc_quick_camera"
                    )
                    QuickActionButton(
                        icon = Icons.Default.ScreenShare,
                        label = "Settings",
                        isActive = false,
                        activeColor = AppleBlue,
                        onClick = onOpenSettings,
                        testTag = "cc_quick_settings"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Note on Android capability
                Text(
                    text = "Controls interact directly with Android system services (Camera torch, Audio, Display, and Settings).",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ConnectivityButton(
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (isActive) activeColor else Color(0x35FFFFFF))
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) Color.White else Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(if (isActive) activeColor else Color(0x352C2C2E))
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color.Black else Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}
