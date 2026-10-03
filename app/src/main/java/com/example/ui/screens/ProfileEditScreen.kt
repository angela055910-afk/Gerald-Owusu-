package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary

private val AvatarPresets = listOf(
    Color(0xFF00A884),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA),
    Color(0xFFD81B60),
    Color(0xFFFB8C00),
    Color(0xFF43A047)
)

private val StatusPresets = listOf(
    "Available",
    "Busy",
    "In an encrypted call 🔒",
    "At work",
    "Exploring Jungle 🌿",
    "Zero-knowledge cipher active"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    currentName: String,
    currentAbout: String,
    currentPhone: String,
    currentAvatarSeed: Int,
    onClose: () -> Unit,
    onSaveProfile: (name: String, about: String, avatarSeed: Int, phone: String) -> Unit
) {
    BackHandler {
        onClose()
    }

    var name by remember { mutableStateOf(currentName) }
    var about by remember { mutableStateOf(currentAbout) }
    var phone by remember { mutableStateOf(currentPhone) }
    var avatarSeed by remember { mutableIntStateOf(currentAvatarSeed) }
    var isEditingName by remember { mutableStateOf(false) }
    var isEditingAbout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
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
                actions = {
                    Button(
                        onClick = {
                            onSaveProfile(name.trim(), about.trim(), avatarSeed, phone.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_profile_button")
                    ) {
                        Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo / Avatar
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable { avatarSeed = (avatarSeed + 1) % AvatarPresets.size }
            ) {
                JungleAvatar(
                    name = if (name.isNotBlank()) name else "User",
                    size = 110.dp,
                    colorSeed = avatarSeed
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(JunglePrimary)
                        .border(2.5.dp, JungleDarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change color",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Avatar Color Preset Selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarPresets.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (avatarSeed == index) 2.5.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .clickable { avatarSeed = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Name Section
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = JunglePrimary)
                            Text(
                                text = "Name",
                                color = Color(0xFF8696A0),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(onClick = { isEditingName = !isEditingName }) {
                            Icon(
                                imageVector = if (isEditingName) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                tint = JunglePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isEditingName) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .testTag("profile_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = JunglePrimary,
                                unfocusedBorderColor = Color(0xFF2A3942),
                                focusedContainerColor = Color(0xFF202C33),
                                unfocusedContainerColor = Color(0xFF202C33)
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = name,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This is not your username or pin. This name will be visible to your Jungle contacts.",
                        color = Color(0xFF667781),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(start = 36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Section
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = JunglePrimary)
                            Text(
                                text = "About",
                                color = Color(0xFF8696A0),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(onClick = { isEditingAbout = !isEditingAbout }) {
                            Icon(
                                imageVector = if (isEditingAbout) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Edit About",
                                tint = JunglePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isEditingAbout) {
                        OutlinedTextField(
                            value = about,
                            onValueChange = { about = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .testTag("profile_about_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = JunglePrimary,
                                unfocusedBorderColor = Color(0xFF2A3942),
                                focusedContainerColor = Color(0xFF202C33),
                                unfocusedContainerColor = Color(0xFF202C33)
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = about,
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Select About Preset",
                        color = Color(0xFF8696A0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 36.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier.padding(start = 36.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusPresets.forEach { preset ->
                            Text(
                                text = preset,
                                color = if (about == preset) JunglePrimary else Color(0xFFE9EDEF),
                                fontSize = 13.sp,
                                fontWeight = if (about == preset) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { about = preset }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Section
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = JunglePrimary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Phone Number",
                            color = Color(0xFF8696A0),
                            fontSize = 12.sp
                        )
                        Text(
                            text = phone,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                    Surface(
                        color = Color(0x3300A884),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Lock, null, tint = EncryptionGold, modifier = Modifier.size(12.dp))
                            Text("Verified E2EE", color = JunglePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
