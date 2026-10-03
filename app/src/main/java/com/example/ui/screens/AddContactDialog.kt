package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onSaveContact: (name: String, phone: String, about: String, startChatNow: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("Available on Jungle") }
    var startChatImmediately by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_contact_dialog"),
            colors = CardDefaults.cardColors(containerColor = JungleDarkSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x3300A884)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = JunglePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "New Contact",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF8696A0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Preview
                JungleAvatar(
                    name = if (name.isNotBlank()) name else "New",
                    size = 64.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Contact Name", color = Color(0xFF8696A0)) },
                    placeholder = { Text("e.g. Jordan Blake", color = Color(0xFF667781)) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = JunglePrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = JunglePrimary,
                        unfocusedBorderColor = Color(0xFF2A3942),
                        focusedContainerColor = Color(0xFF111B21),
                        unfocusedContainerColor = Color(0xFF111B21)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Input
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Phone Number", color = Color(0xFF8696A0)) },
                    placeholder = { Text("e.g. +1 (555) 789-0123", color = Color(0xFF667781)) },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = JunglePrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = JunglePrimary,
                        unfocusedBorderColor = Color(0xFF2A3942),
                        focusedContainerColor = Color(0xFF111B21),
                        unfocusedContainerColor = Color(0xFF111B21)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status / About Input
                OutlinedTextField(
                    value = about,
                    onValueChange = { about = it },
                    label = { Text("About / Status (optional)", color = Color(0xFF8696A0)) },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF8696A0))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_about_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = JunglePrimary,
                        unfocusedBorderColor = Color(0xFF2A3942),
                        focusedContainerColor = Color(0xFF111B21),
                        unfocusedContainerColor = Color(0xFF111B21)
                    ),
                    singleLine = true
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorText ?: "",
                        color = Color(0xFFEA4335),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Checkbox: Message immediately
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = startChatImmediately,
                        onCheckedChange = { startChatImmediately = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = JunglePrimary,
                            uncheckedColor = Color(0xFF8696A0)
                        )
                    )
                    Text(
                        text = "Open encrypted chat immediately",
                        color = Color(0xFFE9EDEF),
                        fontSize = 13.sp
                    )
                }

                // Security Note
                Surface(
                    color = Color(0xFF111B21),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = EncryptionGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "A 256-bit ephemeral keypair is created for secure peer-to-peer verification.",
                            color = Color(0xFF8696A0),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color(0xFF8696A0))
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorText = "Please enter a contact name."
                            } else if (phone.isBlank()) {
                                errorText = "Please enter a phone number."
                            } else {
                                onSaveContact(name.trim(), phone.trim(), about.trim(), startChatImmediately)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_contact_button")
                    ) {
                        Text("Save Contact")
                    }
                }
            }
        }
    }
}
