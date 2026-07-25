package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.NoteEntity
import com.example.data.db.TodoEntity
import com.example.ui.NovaXViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProductivityScreen(viewModel: NovaXViewModel) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf("📝 Notes", "✅ To-Do", "⏱️ Timer & Stopwatch")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Productivity Hub", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        Spacer(modifier = Modifier.height(12.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color.Transparent,
            contentColor = NeonCyan,
            edgePadding = 0.dp
        ) {
            sections.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedSection == idx,
                    onClick = { selectedSection = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSection == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSection == idx) NeonCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedSection) {
            0 -> NotesSubSection(viewModel)
            1 -> TodoSubSection(viewModel)
            2 -> TimerStopwatchSubSection()
        }
    }
}

@Composable
fun NotesSubSection(viewModel: NovaXViewModel) {
    val notes by viewModel.notes.collectAsState()
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Note", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(notes) { note ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(note.title, fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 15.sp)
                            IconButton(
                                onClick = { viewModel.deleteNote(note.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(note.content, color = TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Note", color = NeonCyan) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Content") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (noteTitle.isNotBlank()) {
                        viewModel.addNote(noteTitle, noteContent, "General")
                        noteTitle = ""
                        noteContent = ""
                        showDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TodoSubSection(viewModel: NovaXViewModel) {
    val todos by viewModel.todos.collectAsState()
    var taskInput by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }

    Column(modifier = Modifier.fillMaxSize()) {
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = taskInput,
                    onValueChange = { taskInput = it },
                    placeholder = { Text("Add task...", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (taskInput.isNotBlank()) {
                            viewModel.addTodo(taskInput, priority)
                            taskInput = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NeonCyan)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(todos) { todo ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = todo.isCompleted,
                                onCheckedChange = { viewModel.toggleTodo(todo.id, todo.isCompleted) },
                                colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = todo.task,
                                color = if (todo.isCompleted) TextSecondary else TextPrimary,
                                fontSize = 14.sp
                            )
                        }
                        IconButton(
                            onClick = { viewModel.deleteTodo(todo.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimerStopwatchSubSection() {
    var isStopwatchRunning by remember { mutableStateOf(false) }
    var timeElapsedMillis by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isStopwatchRunning) {
        while (isStopwatchRunning) {
            delay(10)
            timeElapsedMillis += 10
        }
    }

    val minutes = (timeElapsedMillis / 60000)
    val seconds = (timeElapsedMillis % 60000) / 1000
    val millis = (timeElapsedMillis % 1000) / 10

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier.padding(24.dp),
            cornerRadius = 32.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%02d:%02d.%02d", minutes, seconds, millis),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { isStopwatchRunning = !isStopwatchRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isStopwatchRunning) NeonPink else NeonCyan
                        )
                    ) {
                        Text(if (isStopwatchRunning) "Pause" else "Start", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            isStopwatchRunning = false
                            timeElapsedMillis = 0L
                        }
                    ) {
                        Text("Reset", color = TextPrimary)
                    }
                }
            }
        }
    }
}
