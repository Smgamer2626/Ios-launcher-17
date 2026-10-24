package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClockStyle
import com.example.model.DynamicIslandData
import com.example.ui.components.DynamicIsland
import com.example.ui.components.GlassCard
import com.example.ui.theme.AppleYellow
import kotlin.math.roundToInt

@Composable
fun LockScreen(
    timeStr: String,
    dateStr: String,
    clockStyle: ClockStyle,
    clockColor: Color,
    isTorchOn: Boolean,
    dynamicIslandData: DynamicIslandData,
    onToggleTorch: () -> Unit,
    onLaunchCamera: () -> Unit,
    onUnlock: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onToggleDynamicIsland: () -> Unit,
    onTogglePlay: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "unlock_pulse")
    val chevronOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chevron"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("lock_screen")
            .offset { IntOffset(0, dragOffsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        if (dragAmount < 0) {
                            dragOffsetY = (dragOffsetY + dragAmount).coerceAtLeast(-800f)
                        }
                    },
                    onDragEnd = {
                        if (dragOffsetY < -200f) {
                            onUnlock()
                        }
                        dragOffsetY = 0f
                    },
                    onDragCancel = {
                        dragOffsetY = 0f
                    }
                )
            }
    ) {
        // Status Bar safe area & Dynamic Island
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header: Padlock, Dynamic Island, and Control Center notch grabber
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top row with Lock Icon and CC handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                ) {
                    // Small Lock Icon
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Device Secured",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterStart)
                    )

                    // Control Center pull-down bar at top right
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.35f))
                            .clickable { onOpenControlCenter() }
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .testTag("control_center_trigger")
                    ) {
                        Text(
                            text = "Control Center ▾",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Dynamic Island pill
                DynamicIsland(
                    data = dynamicIslandData,
                    onToggleExpand = onToggleDynamicIsland,
                    onTogglePlay = onTogglePlay,
                    onNextTrack = onNextTrack,
                    onPrevTrack = onPrevTrack,
                    onToggleTimer = onToggleTimer,
                    onResetTimer = onResetTimer,
                    onToggleTorch = onToggleTorch
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Date Line
                Text(
                    text = dateStr.ifEmpty { "Saturday, October 24" },
                    color = clockColor.copy(alpha = 0.9f),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Dynamic Large iOS Clock
                Text(
                    text = timeStr.ifEmpty { "09:41" },
                    style = TextStyle(
                        fontFamily = clockStyle.fontFamily,
                        fontWeight = clockStyle.fontWeight,
                        fontSize = 86.sp,
                        letterSpacing = clockStyle.letterSpacing.sp,
                        color = clockColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive iOS Notification Preview
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = Color(0x351C1C1E),
                    onClick = { onUnlock() }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF34C759)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "MESSAGES",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "now",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "Welcome to iOS Nova",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Swipe up or tap here to experience your customized iPhone interface.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Bottom Section: Flashlight & Camera circular buttons + Swipe up prompt
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Flashlight and Camera Quick Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 42.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flashlight button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(elevation = 10.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (isTorchOn) AppleYellow else Color(0x55000000))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = Color.White),
                                onClick = onToggleTorch
                            )
                            .testTag("lock_flashlight_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight",
                            tint = if (isTorchOn) Color.Black else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Camera button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(elevation = 10.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(Color(0x55000000))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = Color.White),
                                onClick = onLaunchCamera
                            )
                            .testTag("lock_camera_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Swipe Up Bar and Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onUnlock() }
                        .padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Swipe up",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .offset(y = chevronOffset.dp)
                            .size(20.dp)
                    )
                    Text(
                        text = "Swipe up to open",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Home indicator pill
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        }
    }
}
