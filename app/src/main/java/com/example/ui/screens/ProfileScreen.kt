package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserProfileEntity
import com.example.ui.NovaXViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: NovaXViewModel) {
    val userProfileState by viewModel.userProfile.collectAsState()
    val profile = userProfileState ?: UserProfileEntity()

    var userName by remember(profile) { mutableStateOf(profile.name) }
    var userEmail by remember(profile) { mutableStateOf(profile.email) }
    var selectedPersonality by remember(profile) { mutableStateOf(profile.personality) }
    var selectedLanguage by remember(profile) { mutableStateOf(profile.preferredLanguage) }
    var darkTheme by remember(profile) { mutableStateOf(profile.darkThemeEnabled) }
    var isEncrypted by remember(profile) { mutableStateOf(profile.isEncrypted) }

    val context = LocalContext.current

    val personalities = listOf("Friendly & Professional", "Developer & Hacker", "Creative Storyteller", "Academic Scholar")
    val languages = listOf("English", "Hindi", "Urdu", "Bengali", "French", "German", "Spanish", "Japanese")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("User Profile & AI Controls", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            Text("Customize Nova X Personality, Security & Preferences", fontSize = 12.sp, color = TextSecondary)
        }

        // Profile Card Header
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(userEmail, fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Session Status: Secure & Encrypted", fontSize = 11.sp, color = NeonCyan)
                    }
                }
            }
        }

        // Personality & Language Settings
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("AI Assistant Personality", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 14.sp)
                    personalities.forEach { pers ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedPersonality == pers,
                                onClick = { selectedPersonality = pers },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Text(pers, fontSize = 13.sp, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Preferred System Language", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 14.sp)

                    var expandedLang by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedLang,
                        onExpandedChange = { expandedLang = !expandedLang }
                    ) {
                        OutlinedTextField(
                            value = selectedLanguage,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLang) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedLang,
                            onDismissRequest = { expandedLang = false }
                        ) {
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang) },
                                    onClick = {
                                        selectedLanguage = lang
                                        expandedLang = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Security & Theme Toggles
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Security & Display", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 14.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("AES-256 Local Data Encryption", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = isEncrypted,
                            onCheckedChange = { isEncrypted = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Cyber Theme", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { darkTheme = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                        )
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    viewModel.updateProfile(
                        name = userName,
                        email = userEmail,
                        personality = selectedPersonality,
                        language = selectedLanguage,
                        darkTheme = darkTheme
                    )
                    Toast.makeText(context, "Settings updated successfully!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("Save Profile & AI Settings", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
