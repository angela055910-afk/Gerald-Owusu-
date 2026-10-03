package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JunglePrimary
import com.example.ui.theme.ReadReceiptBlue

private val AvatarColors = listOf(
    Color(0xFF00A884),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA),
    Color(0xFFD81B60),
    Color(0xFFE53935),
    Color(0xFFFB8C00),
    Color(0xFF43A047)
)

@Composable
fun JungleAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    hasStory: Boolean = false,
    storyViewed: Boolean = false,
    colorSeed: Int = 0,
    isGroup: Boolean = false
) {
    val initial = if (name.isBlank()) "?" else name.trim().take(1).uppercase()
    val bgColor = AvatarColors[Math.abs(colorSeed) % AvatarColors.size]

    val outerModifier = if (hasStory) {
        modifier
            .size(size)
            .border(
                width = 2.5.dp,
                brush = Brush.linearGradient(
                    if (storyViewed) listOf(Color(0xFF8696A0), Color(0xFF667781))
                    else listOf(JunglePrimary, Color(0xFF25D366))
                ),
                shape = CircleShape
            )
            .padding(3.dp)
    } else {
        modifier.size(size)
    }

    Box(
        modifier = outerModifier
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isGroup) "👥" else initial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.42f).sp
        )
    }
}

@Composable
fun ReadReceiptIcon(status: String, modifier: Modifier = Modifier) {
    when (status) {
        "READ" -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read",
                tint = ReadReceiptBlue,
                modifier = modifier.size(17.dp)
            )
        }
        "DELIVERED" -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Delivered",
                tint = Color(0xFF8696A0),
                modifier = modifier.size(17.dp)
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                tint = Color(0xFF8696A0),
                modifier = modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EncryptionBadge(
    modifier: Modifier = Modifier,
    text: String = "End-to-End Encrypted"
) {
    Row(
        modifier = modifier
            .background(Color(0x22FFD54F), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = EncryptionGold,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text,
            color = EncryptionGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AudioWaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 18,
    activeColor: Color = JunglePrimary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveformPulse"
    )

    Row(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(4.dp)),
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val staticHeights = listOf(
            0.3f, 0.6f, 0.9f, 0.4f, 0.8f, 1.0f, 0.5f, 0.7f, 0.9f,
            0.3f, 0.8f, 0.6f, 0.95f, 0.4f, 0.7f, 0.5f, 0.85f, 0.4f
        )

        for (i in 0 until barCount) {
            val baseHeight = staticHeights.getOrElse(i % staticHeights.size) { 0.5f }
            val heightFraction = if (isPlaying) {
                val offset = (i % 3) * 0.15f
                ((baseHeight * animatedProgress + offset) % 1f).coerceIn(0.2f, 1.0f)
            } else {
                baseHeight
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(heightFraction)
                    .clip(RoundedCornerShape(2.dp))
                    .background(activeColor)
            )
        }
    }
}
