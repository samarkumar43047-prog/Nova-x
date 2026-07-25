package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NovaXViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan

class MainActivity : ComponentActivity() {

    private val viewModel: NovaXViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsState()
            val isDark = userProfile?.darkThemeEnabled ?: true

            MyApplicationTheme(darkTheme = isDark) {
                var selectedIndex by remember { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        GlassmorphicBottomBar(
                            selectedIndex = selectedIndex,
                            onTabSelected = { selectedIndex = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedIndex) {
                            0 -> ChatScreen(viewModel = viewModel)
                            1 -> StudioScreen(viewModel = viewModel)
                            2 -> ProductivityScreen(viewModel = viewModel)
                            3 -> UtilityScreen(viewModel = viewModel)
                            4 -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun GlassmorphicBottomBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Chat", Icons.Default.ChatBubble),
        NavItem("Studio", Icons.Default.AutoAwesome),
        NavItem("Tasks", Icons.Default.CheckCircle),
        NavItem("Tools", Icons.Default.GridOn),
        NavItem("Profile", Icons.Default.Person)
    )

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) NeonCyan else Color(0xFF64748B)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            color = if (isSelected) NeonCyan else Color(0xFF64748B)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = NeonCyan.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}
