package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.CallEndRed
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JunglePrimary
import com.example.ui.viewmodel.ActiveCallState

@Composable
fun CallScreen(
    call: ActiveCallState,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleVideo: () -> Unit
) {
    BackHandler {
        onEndCall()
    }

    var showKeyDetails by remember { mutableStateOf(false) }
    var isScreenSharing by remember { mutableStateOf(false) }
    var showLinkCopied by remember { mutableStateOf(false) }

    val formattedDuration = remember(call.durationSec) {
        val minutes = call.durationSec / 60
        val seconds = call.durationSec % 60
        "%02d:%02d".format(minutes, seconds)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF071B16),
                        JungleDarkBackground,
                        Color(0xFF030A08)
                    )
                )
            )
            .testTag("call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: E2EE Indicator & Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Surface(
                    color = Color(0x3300A884),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5500A884))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = JunglePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "End-to-End Encrypted (AES-256-GCM)",
                            color = Color(0xFFE9EDEF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = call.contactName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (call.isConnected) formattedDuration else "Connecting encrypted session…",
                    fontSize = 15.sp,
                    color = if (call.isConnected) JunglePrimary else Color(0xFF8696A0),
                    fontWeight = FontWeight.Medium
                )
            }

            // Center: Avatar or Video Feed
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                if (call.callType == "VIDEO" && !call.isVideoDisabled) {
                    // Simulated Video Stream Surface
                    Box(
                        modifier = Modifier
                            .size(280.dp, 380.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1F2C34))
                            .border(2.dp, Color(0x4400A884), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            JungleAvatar(
                                name = call.contactName,
                                size = 96.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Encrypted 1080p Video Stream",
                                color = Color(0xFF8696A0),
                                fontSize = 12.sp
                            )
                        }

                        // Picture in picture local view preview
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(70.dp, 95.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0B141A))
                                .border(1.dp, JunglePrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "You",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Voice Call Pulsing Avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.scale(if (!call.isConnected) pulseScale else 1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .clip(CircleShape)
                                .background(Color(0x1500A884))
                        )
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color(0x2500A884))
                        )
                        JungleAvatar(
                            name = call.contactName,
                            size = 110.dp
                        )
                    }
                }
            }

            // Audio Waveform when connected on voice
            if (call.isConnected && call.callType == "VOICE") {
                AudioWaveformVisualizer(
                    isPlaying = !call.isMuted,
                    barCount = 24,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // Fingerprint info sheet
            AnimatedVisibility(visible = showKeyDetails) {
                Surface(
                    color = Color(0xFF111B21),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ephemeral Handshake Fingerprint",
                            color = EncryptionGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = call.safetyFingerprint,
                            color = Color(0xFFE9EDEF),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Screen share banner
            AnimatedVisibility(visible = isScreenSharing) {
                Surface(
                    color = Color(0x3300A884),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JunglePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ScreenShare,
                            contentDescription = null,
                            tint = JunglePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "You are sharing your screen • E2EE Streamed",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Link copied toast banner
            AnimatedVisibility(visible = showLinkCopied) {
                Surface(
                    color = Color(0xFF202C33),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "🔗 Call Link copied: https://jungle.chat/call/enc-${call.contactName.hashCode().toString().takeLast(6)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Controls Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    IconButton(
                        onClick = onToggleMute,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (call.isMuted) Color.White else Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("call_mute_button")
                    ) {
                        Icon(
                            imageVector = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (call.isMuted) Color.Black else Color.White
                        )
                    }

                    // Speaker Button
                    IconButton(
                        onClick = onToggleSpeaker,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (call.isSpeaker) JunglePrimary else Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("call_speaker_button")
                    ) {
                        Icon(
                            imageVector = if (call.isSpeaker) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Speaker",
                            tint = Color.White
                        )
                    }

                    // Video Toggle Button
                    if (call.callType == "VIDEO") {
                        IconButton(
                            onClick = onToggleVideo,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (call.isVideoDisabled) Color.White else Color(0x33FFFFFF)
                            ),
                            modifier = Modifier
                                .size(50.dp)
                                .testTag("call_video_toggle")
                        ) {
                            Icon(
                                imageVector = if (call.isVideoDisabled) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Camera Toggle",
                                tint = if (call.isVideoDisabled) Color.Black else Color.White
                            )
                        }
                    }

                    // Screen Share Toggle (WhatsApp 2026 feature)
                    IconButton(
                        onClick = { isScreenSharing = !isScreenSharing },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isScreenSharing) JunglePrimary else Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("call_screen_share_button")
                    ) {
                        Icon(
                            imageVector = if (isScreenSharing) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                            contentDescription = "Screen Share",
                            tint = Color.White
                        )
                    }

                    // Share Call Link button
                    IconButton(
                        onClick = { showLinkCopied = !showLinkCopied },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (showLinkCopied) JunglePrimary else Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("call_share_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Call Link",
                            tint = Color.White
                        )
                    }

                    // Security Fingerprint Toggle
                    IconButton(
                        onClick = { showKeyDetails = !showKeyDetails },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (showKeyDetails) EncryptionGold else Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("call_security_info")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security details",
                            tint = if (showKeyDetails) Color.Black else Color.White
                        )
                    }
                }

                // End Call Floating Action Button
                FloatingActionButton(
                    onClick = onEndCall,
                    containerColor = CallEndRed,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("end_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
