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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Contact
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary

private val GroupThemeColors = listOf(
    Color(0xFF00A884), // Emerald Jungle
    Color(0xFF1E88E5), // Cobalt Blue
    Color(0xFF8E24AA), // Deep Purple
    Color(0xFFD81B60), // Crimson Rose
    Color(0xFFFB8C00), // Amber Gold
    Color(0xFF43A047)  // Forest Green
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGroupScreen(
    contacts: List<Contact>,
    onClose: () -> Unit,
    onCreateGroup: (name: String, description: String, selectedContactNames: List<String>, iconColorSeed: Int) -> Unit
) {
    BackHandler {
        onClose()
    }

    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedColorSeed by remember { mutableIntStateOf(0) }
    val selectedContactNames = remember { mutableStateListOf<String>() }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.phoneNumber.contains(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "New Encrypted Group",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${selectedContactNames.size} of ${contacts.size} members selected",
                            color = Color(0xFF8696A0),
                            fontSize = 12.sp
                        )
                    }
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
        floatingActionButton = {
            if (groupName.isNotBlank() && selectedContactNames.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        onCreateGroup(
                            groupName.trim(),
                            groupDescription.trim(),
                            selectedContactNames.toList(),
                            selectedColorSeed
                        )
                    },
                    containerColor = JunglePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("create_group_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Create Group",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        containerColor = JungleDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // E2EE Notice Banner
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = EncryptionGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Group chats use multi-party Sender Keys ratcheting. Messages are encrypted before leaving your phone.",
                        color = Color(0xFF8696A0),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Group Avatar Preview & Subject Input
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GroupThemeColors[selectedColorSeed % GroupThemeColors.size])
                        .border(2.dp, Color(0x55FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    placeholder = { Text("Group Subject", color = Color(0xFF8696A0)) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("group_name_input"),
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
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Group Color Seed Palette Picker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            ) {
                Text(
                    text = "Icon Color:",
                    color = Color(0xFF8696A0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                GroupThemeColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (selectedColorSeed == index) 2.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .clickable { selectedColorSeed = index }
                    )
                }
            }

            // Description Input
            OutlinedTextField(
                value = groupDescription,
                onValueChange = { groupDescription = it },
                placeholder = { Text("Group description (optional)", color = Color(0xFF8696A0)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("group_desc_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = JunglePrimary,
                    unfocusedBorderColor = Color(0xFF2A3942),
                    focusedContainerColor = Color(0xFF111B21),
                    unfocusedContainerColor = Color(0xFF111B21)
                ),
                maxLines = 2
            )

            // Selected participants horizontal chips
            if (selectedContactNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(selectedContactNames) { contactName ->
                        Surface(
                            color = Color(0xFF202C33),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3942))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                JungleAvatar(name = contactName, size = 22.dp)
                                Text(
                                    text = contactName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { selectedContactNames.remove(contactName) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Contacts for Group Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search contacts to add…", color = Color(0xFF8696A0), fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8696A0), modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, null, tint = Color(0xFF8696A0), modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("group_contact_search_input"),
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select Participants (${filteredContacts.size})",
                color = JunglePrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Contact Picker List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredContacts, key = { it.id }) { contact ->
                    val isSelected = selectedContactNames.contains(contact.name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x2200A884) else Color(0xFF111B21))
                            .clickable {
                                if (isSelected) {
                                    selectedContactNames.remove(contact.name)
                                } else {
                                    selectedContactNames.add(contact.name)
                                }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            JungleAvatar(name = contact.name, size = 42.dp)
                            Column {
                                Text(
                                    text = contact.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = contact.phoneNumber,
                                    color = Color(0xFF8696A0),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = contact.statusAbout,
                                    color = Color(0xFF667781),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) selectedContactNames.add(contact.name)
                                else selectedContactNames.remove(contact.name)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = JunglePrimary,
                                uncheckedColor = Color(0xFF8696A0)
                            )
                        )
                    }
                }
            }
        }
    }
}
