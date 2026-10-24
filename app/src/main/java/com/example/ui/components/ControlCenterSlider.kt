package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleGlassBorderDark
import com.example.ui.theme.AppleGlassDark

@Composable
fun ControlCenterSlider(
    value: Float, // 0.0f to 1.0f
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    contentDescription: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var componentHeight = 0

    Box(
        modifier = modifier
            .testTag(testTag)
            .width(68.dp)
            .height(154.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0x352C2C2E))
            .onSizeChanged { componentHeight = it.height }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (componentHeight > 0) {
                        val newFraction = 1f - (offset.y / componentHeight.toFloat())
                        onValueChange(newFraction.coerceIn(0f, 1f))
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    if (componentHeight > 0) {
                        val newFraction = 1f - (change.position.y / componentHeight.toFloat())
                        onValueChange(newFraction.coerceIn(0f, 1f))
                    }
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Filled vertical bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(value.coerceIn(0f, 1f))
                .background(Color.White)
        )

        // Icon at the bottom (inverted color when fill is high, white when low)
        val iconTint = if (value > 0.22f) Color(0xFF1C1C1E) else Color.White

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 18.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }

        // Percentage tooltip at the very top if high
        if (value > 0.85f) {
            Text(
                text = "${(value * 100).toInt()}%",
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
        }
    }
}
