package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NovaXViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*

@Composable
fun StudioScreen(viewModel: NovaXViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("💻 Coding Hub", "✍️ Writing Studio", "🖼️ AI Image Suite", "📄 Document Reader")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Nova X AI Studio",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )
        Text(
            text = "Professional AI Tools for Developers, Writers & Creators",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = NeonCyan,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) NeonCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> CodingHubSection(viewModel)
            1 -> WritingStudioSection(viewModel)
            2 -> ImageSuiteSection(viewModel)
            3 -> DocumentReaderSection(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodingHubSection(viewModel: NovaXViewModel) {
    var codePrompt by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("Python") }
    var selectedMode by remember { mutableStateOf("Generate") }
    val codeOutput by viewModel.codeOutput.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val languages = listOf("Python", "Kotlin", "Java", "C++", "C", "JavaScript", "HTML/CSS", "SQL", "PHP")
    val modes = listOf("Generate", "Debug", "Explain", "Optimize")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Select Language:", fontSize = 12.sp, color = TextSecondary)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(languages) { lang ->
                    FilterChip(
                        selected = selectedLang == lang,
                        onClick = { selectedLang = lang },
                        label = { Text(lang, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        item {
            Text("Select Action:", fontSize = 12.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                modes.forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        label = { Text(mode, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = codePrompt,
                onValueChange = { codePrompt = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                placeholder = { Text("Enter prompt or paste code to debug/explain...", color = TextSecondary, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        item {
            Button(
                onClick = { viewModel.executeCodingTask(codePrompt, selectedLang, selectedMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                } else {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Execute Code Action ($selectedMode)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (codeOutput.isNotBlank()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Output Result ($selectedLang)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(codeOutput))
                                Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = codeOutput,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingStudioSection(viewModel: NovaXViewModel) {
    var writingTopic by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Email") }
    val writingOutput by viewModel.writingOutput.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val writingTypes = listOf(
        "Email", "Essay", "Story", "Shayari", "Poetry", "Resume", "Cover Letter",
        "Speech", "Caption", "Blog", "Notes", "Script", "Translation", "Grammar Corrector", "Summarization"
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Writing Category:", fontSize = 12.sp, color = TextSecondary)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(writingTypes) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPink,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = writingTopic,
                onValueChange = { writingTopic = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Topic, key points, or text to correct/translate...", color = TextSecondary, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        item {
            Button(
                onClick = { viewModel.executeWritingTask(writingTopic, selectedType) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Draft $selectedType", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (writingOutput.isNotBlank()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$selectedType Draft", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(writingOutput))
                                Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonPink)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = writingOutput, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageSuiteSection(viewModel: NovaXViewModel) {
    var imagePrompt by remember { mutableStateOf("") }
    var generatedResult by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Image Prompt & Enhancement", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = imagePrompt,
                        onValueChange = { imagePrompt = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Describe image scene, art style, subject...", color = TextSecondary, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            generatedResult = "🎨 **AI Image Prompt Refined**:\n\"Ultra-realistic cyberpunk concept art of ${imagePrompt.ifBlank { "a futuristic space station" }}, 8k resolution, raytraced lighting, neon cyan details, photorealistic.\""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("Generate & Enhance Prompt", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (generatedResult.isNotBlank()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = generatedResult,
                        modifier = Modifier.padding(14.dp),
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentReaderSection(viewModel: NovaXViewModel) {
    val docText by viewModel.documentText.collectAsState()
    val docSummary by viewModel.documentSummary.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search inside document...", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Document Preview (PDF / DOCX / TXT)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = docText, color = TextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.summarizeDocument(docText) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Summarize & Extract Insights", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (docSummary.isNotBlank()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("AI Summary & Key Takeaways", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = docSummary, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
