package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.DynamicIslandData
import com.example.ui.components.CalendarWidget
import com.example.ui.components.DynamicIsland
import com.example.ui.components.GlassCard
import com.example.ui.components.HomeDock
import com.example.ui.components.SquircleIcon
import com.example.ui.components.WeatherWidget
import com.example.ui.theme.AppleBlue

@Composable
fun HomeScreen(
    apps: List<AppItem>,
    dockApps: List<AppItem>,
    dynamicIslandData: DynamicIslandData,
    dateStr: String,
    isEditMode: Boolean,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onExitEditMode: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onLockScreen: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleDynamicIsland: () -> Unit,
    onTogglePlay: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onToggleTorch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar / Dynamic Island Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lock shortcut
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x35000000))
                        .clickable { onLockScreen() }
                        .testTag("home_lock_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Phone",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Dynamic Island
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

                // Control Center & Settings shortcut
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable { onOpenControlCenter() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("home_control_center_trigger")
                    ) {
                        Text(
                            text = "Control ▾",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Edit Mode Bar (if user long-pressed to jiggle)
            AnimatedVisibility(
                visible = isEditMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Mode Active (Jiggle)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppleBlue)
                            .clickable { onExitEditMode() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("done_edit_mode_button")
                    ) {
                        Text(
                            text = "Done",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Content: Widgets & App Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Top Row: 2x2 Widgets (Weather and Calendar)
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    WeatherWidget(
                        onClick = { onToggleDynamicIsland() }
                    )
                }

                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    CalendarWidget(
                        dateStr = dateStr,
                        onClick = { onOpenSettings() }
                    )
                }

                // Grid Apps
                items(apps) { app ->
                    SquircleIcon(
                        app = app,
                        size = 60.dp,
                        showLabel = true,
                        isEditMode = isEditMode,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) }
                    )
                }
            }

            // iOS Page Indicator Dots
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x35000000))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f))
                )
            }

            // Bottom Dock
            HomeDock(
                dockApps = dockApps,
                isEditMode = isEditMode,
                onAppClick = onAppClick,
                onAppLongClick = onAppLongClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Bottom Home indicator bar
            Box(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .width(140.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
    }
}
