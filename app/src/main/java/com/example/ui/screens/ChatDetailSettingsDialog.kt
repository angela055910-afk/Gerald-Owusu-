package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.Chat
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.EchoStreamTeal
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary

data class WallpaperTheme(val category: String, val palettes: List<Pair<String, String>>)

private val ThemeCategories = listOf(
    WallpaperTheme("🌿 Nature", listOf(
        Pair("Emerald", "#0B2923"),
        Pair("Bamboo", "#004D40"),
        Pair("Rainforest", "#1B3830")
    )),
    WallpaperTheme("✨ Live", listOf(
        Pair("Cyan Wave", "#003B46"),
        Pair("Cyber Glow", "#0A2239"),
        Pair("Aurora", "#1A2E40")
    )),
    WallpaperTheme("⬛ Minimal", listOf(
        Pair("Midnight", "#0B141A"),
        Pair("Nightfall", "#111B21"),
        Pair("Charcoal", "#1A1A1A")
    )),
    WallpaperTheme("🎨 Doodle", listOf(
        Pair("Classic", "#121B22"),
        Pair("Jungle Art", "#0E2A20"),
        Pair("Matrix", "#071E18")
    ))
)

private val DisappearingDurations = listOf("Off", "24 hours", "7 days", "90 days")

@Composable
fun ChatDetailSettingsDialog(
    chat: Chat,
    currentWallpaperHex: String,
    onDismiss: () -> Unit,
    onClearChat: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleMute: () -> Unit,
    onUpdateGroupInfo: (name: String, desc: String) -> Unit,
    onSelectWallpaper: (hex: String) -> Unit
) {
    var groupSubject by remember { mutableStateOf(chat.name) }
    var groupDescription by remember { mutableStateOf(chat.groupDescription) }
    var isEditingGroupInfo by remember { mutableStateOf(false) }
    var isPinned by remember { mutableStateOf(chat.isPinned) }
    var isMuted by remember { mutableStateOf(chat.isMuted) }
    var selectedDuration by remember { mutableStateOf("Off") }
    var showConfirmClear by remember { mutableStateOf(false) }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var isGeneratingAiWallpaper by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("chat_settings_dialog"),
            colors = CardDefaults.cardColors(containerColor = JungleDarkSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        JungleAvatar(name = chat.name, size = 42.dp, isGroup = chat.isGroup)
                        Column {
                            Text(
                                text = if (chat.isGroup) "Group Settings" else "Chat Options",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = chat.name,
                                color = Color(0xFF8696A0),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8696A0))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Group Edit section if group chat
                if (chat.isGroup) {
                    Surface(
                        color = Color(0xFF111B21),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Group, null, tint = JunglePrimary, modifier = Modifier.size(20.dp))
                                    Text("Group Information", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                                IconButton(onClick = { isEditingGroupInfo = !isEditingGroupInfo }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Edit, "Edit", tint = JunglePrimary, modifier = Modifier.size(16.dp))
                                }
                            }

                            if (isEditingGroupInfo) {
                                OutlinedTextField(
                                    value = groupSubject,
                                    onValueChange = { groupSubject = it },
                                    label = { Text("Group Subject", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
                                OutlinedTextField(
                                    value = groupDescription,
                                    onValueChange = { groupDescription = it },
                                    label = { Text("Group Description", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = JunglePrimary,
                                        unfocusedBorderColor = Color(0xFF2A3942),
                                        focusedContainerColor = Color(0xFF202C33),
                                        unfocusedContainerColor = Color(0xFF202C33)
                                    ),
                                    maxLines = 2
                                )
                                Button(
                                    onClick = {
                                        onUpdateGroupInfo(groupSubject, groupDescription)
                                        isEditingGroupInfo = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                                ) {
                                    Text("Update", fontSize = 12.sp, color = Color.Black)
                                }
                            } else {
                                Text(
                                    text = chat.groupDescription.ifBlank { "No description set" },
                                    color = Color(0xFF8696A0),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Chat Wallpaper Selector
                // Chat Wallpaper Theme (WhatsApp 2026 4-Category System)
                Surface(
                    color = Color(0xFF111B21),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Wallpaper, null, tint = JunglePrimary, modifier = Modifier.size(20.dp))
                                Text("Chat Themes (2026)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }

                            // AI Wallpaper Generator Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x3300F5D4),
                                modifier = Modifier.clickable {
                                    isGeneratingAiWallpaper = true
                                    val aiColors = listOf("#004D40", "#003B46", "#1B2A32", "#132E24")
                                    onSelectWallpaper(aiColors.random())
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = EchoStreamTeal, modifier = Modifier.size(12.dp))
                                    Text("AI Theme", color = EchoStreamTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ThemeCategories.forEachIndexed { index, theme ->
                                val isSelected = selectedCategoryIndex == index
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0x3300A884) else Color(0xFF202C33),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, JunglePrimary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedCategoryIndex = index }
                                ) {
                                    Text(
                                        text = theme.category,
                                        color = if (isSelected) JunglePrimary else Color(0xFF8696A0),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Wallpaper choices for selected category
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ThemeCategories[selectedCategoryIndex].palettes.forEach { (label, hex) ->
                                val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { JunglePrimary }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color)
                                            .border(
                                                width = if (currentWallpaperHex == hex) 2.5.dp else 1.dp,
                                                color = if (currentWallpaperHex == hex) JunglePrimary else Color(0x33FFFFFF),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onSelectWallpaper(hex) }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(label, color = Color(0xFF8696A0), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Disappearing Messages
                Surface(
                    color = Color(0xFF111B21),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.HourglassBottom, null, tint = EncryptionGold, modifier = Modifier.size(20.dp))
                            Text("Disappearing Messages", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DisappearingDurations.forEach { dur ->
                                Surface(
                                    color = if (selectedDuration == dur) Color(0x3300A884) else Color(0xFF202C33),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (selectedDuration == dur) JunglePrimary else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDuration = dur }
                                ) {
                                    Text(
                                        text = dur,
                                        color = if (selectedDuration == dur) JunglePrimary else Color(0xFF8696A0),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pin & Mute Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PushPin, null, tint = Color(0xFF8696A0), modifier = Modifier.size(20.dp))
                        Text("Pin Chat", color = Color.White, fontSize = 14.sp)
                    }
                    Switch(
                        checked = isPinned,
                        onCheckedChange = {
                            isPinned = it
                            onTogglePin()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = JunglePrimary)
                    )
                }

                HorizontalDivider(color = Color(0xFF202C33), modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.NotificationsOff, null, tint = Color(0xFF8696A0), modifier = Modifier.size(20.dp))
                        Text("Mute Notifications", color = Color.White, fontSize = 14.sp)
                    }
                    Switch(
                        checked = isMuted,
                        onCheckedChange = {
                            isMuted = it
                            onToggleMute()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = JunglePrimary)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clear Chat action
                if (showConfirmClear) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33EA4335), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Are you sure you want to clear all messages in this chat?",
                            color = Color(0xFFEA4335),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { showConfirmClear = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("Cancel", color = Color(0xFF8696A0), fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    showConfirmClear = false
                                    onClearChat()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335))
                            ) {
                                Text("Clear Messages", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFF111B21),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showConfirmClear = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, null, tint = Color(0xFFEA4335), modifier = Modifier.size(20.dp))
                            Text("Clear Chat History", color = Color(0xFFEA4335), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
