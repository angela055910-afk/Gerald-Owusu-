package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.EchoStreamTeal
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userName: String = "Angela",
    userAbout: String = "Exploring Jungle & EchoStream 🌿",
    userPhone: String = "+1 (555) 789-0123",
    userAvatarSeed: Int = 0,
    onOpenProfile: () -> Unit = {},
    onOpenRegisterNew: () -> Unit = {},
    onClose: () -> Unit
) {
    BackHandler {
        onClose()
    }

    var readReceiptsEnabled by remember { mutableStateOf(true) }
    var e2eeCallRelayOnly by remember { mutableStateOf(true) }
    var biometricLockEnabled by remember { mutableStateOf(false) }
    var echoStreamAiAssistance by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JungleDarkSurface)
            )
        },
        containerColor = JungleDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card - Clickable to open ProfileEditScreen
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenProfile() }
                    .testTag("settings_profile_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            JungleAvatar(name = userName, size = 56.dp, colorSeed = userAvatarSeed)
                            Column {
                                Text(
                                    text = userName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = userAbout,
                                    color = Color(0xFF8696A0),
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = userPhone,
                                    color = JunglePrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(onClick = onOpenProfile) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = JunglePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onOpenProfile,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202C33)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = JunglePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "  Edit Profile & Status",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Account & Phone Switch Section
            Text(
                text = "Account & Registration",
                color = Color(0xFF8696A0),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenRegisterNew() }
                            .padding(vertical = 12.dp)
                            .testTag("settings_register_new_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = JunglePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Register New / Switch Account",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "WhatsApp-style phone onboarding with SMS OTP & cipher keys",
                                color = Color(0xFF8696A0),
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "Switch",
                            color = JunglePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // EchoStream AI Engine Card
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3300F5D4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = EchoStreamTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "EchoStream AI Privacy Engine",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Summaries, Smart Drafts & Q&A",
                                    color = Color(0xFF8696A0),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Switch(
                            checked = echoStreamAiAssistance,
                            onCheckedChange = { echoStreamAiAssistance = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = JunglePrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "EchoStream provides real-time thread summarization and context drafts with zero data retention. Your prompt history never trains third-party models.",
                        color = Color(0xFF8696A0),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            // Privacy & Security Section
            Text(
                text = "Privacy & Encryption",
                color = Color(0xFF8696A0),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SettingsToggleRow(
                        icon = Icons.Default.Check,
                        title = "Read Receipts",
                        subtitle = "If turned off, you won't send or receive double blue checks",
                        checked = readReceiptsEnabled,
                        onCheckedChange = { readReceiptsEnabled = it }
                    )

                    HorizontalDivider(color = Color(0xFF202C33))

                    SettingsToggleRow(
                        icon = Icons.Default.Lock,
                        title = "Peer-to-Peer Calls",
                        subtitle = "Relay voice/video calls through Jungle blind servers to prevent IP address exposure",
                        checked = e2eeCallRelayOnly,
                        onCheckedChange = { e2eeCallRelayOnly = it }
                    )

                    HorizontalDivider(color = Color(0xFF202C33))

                    SettingsToggleRow(
                        icon = Icons.Default.Security,
                        title = "Biometric App Lock",
                        subtitle = "Require fingerprint or face unlock when opening Jungle",
                        checked = biometricLockEnabled,
                        onCheckedChange = { biometricLockEnabled = it }
                    )
                }
            }

            // General Settings
            Text(
                text = "General",
                color = Color(0xFF8696A0),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SettingsNavRow(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Message sounds, call ringtones, high-priority alerts"
                    )

                    HorizontalDivider(color = Color(0xFF202C33))

                    SettingsNavRow(
                        icon = Icons.Default.Storage,
                        title = "Storage & Data",
                        subtitle = "Network usage, auto-download media over Wi-Fi"
                    )

                    HorizontalDivider(color = Color(0xFF202C33))

                    SettingsNavRow(
                        icon = Icons.Default.Info,
                        title = "About Jungle & Protocol",
                        subtitle = "v2.4.0 • Curve25519 Double Ratchet E2EE"
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = JunglePrimary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF8696A0),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = JunglePrimary
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Nav action */ }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8696A0),
            modifier = Modifier.size(22.dp)
        )
        Column {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = Color(0xFF8696A0),
                fontSize = 11.sp
            )
        }
    }
}
