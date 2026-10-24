package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem

@Composable
fun SquircleIcon(
    app: AppItem,
    size: Dp = 60.dp,
    showLabel: Boolean = true,
    isEditMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jiggle")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 140, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble"
    )

    val currentRotation = if (isEditMode) rotation else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .rotate(currentRotation)
            .pointerInput(isEditMode) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            }
            .testTag("app_icon_${app.id}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            // Squircle rounded background with iOS gradient
            Box(
                modifier = Modifier
                    .size(size)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = app.gradientColors,
                            start = Offset(0f, 0f),
                            end = Offset(100f, 100f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Vector glyph representing the app
                val iconVector = when (app.iconName) {
                    "phone" -> Icons.Default.Call
                    "safari" -> Icons.Default.Explore
                    "messages" -> Icons.Default.Chat
                    "camera" -> Icons.Default.PhotoCamera
                    "photos" -> Icons.Default.PhotoLibrary
                    "weather" -> Icons.Default.WbSunny
                    "clock" -> Icons.Default.Schedule
                    "calculator" -> Icons.Default.Calculate
                    "notes" -> Icons.Default.StickyNote2
                    "music" -> Icons.Default.MusicNote
                    "maps" -> Icons.Default.Map
                    "calendar" -> Icons.Default.CalendarMonth
                    "settings" -> Icons.Default.Settings
                    "files" -> Icons.Default.Folder
                    "health" -> Icons.Default.Favorite
                    "podcasts" -> Icons.Default.Headphones
                    else -> Icons.Default.Apps
                }

                Icon(
                    imageVector = iconVector,
                    contentDescription = app.name,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.55f)
                )
            }

            // iOS-style minus badge in edit mode
            if (isEditMode && !app.isDock) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-3).dp, y = (-3).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8E8E93)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.name,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
