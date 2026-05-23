package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.VibeViewModel
import com.example.ui.screens.*
import com.example.ui.theme.VibeStudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VibeViewModel = viewModel()
            val profile by viewModel.userProfile.collectAsState()

            VibeStudyTheme(selectedThemeIndex = profile.selectedThemeIndex) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppScaffold(viewModel: VibeViewModel) {
    var activeBottomTab by remember { mutableStateOf(0) } // 0: Home, 1: Planner, 2: Timer, 3: Aura, 4: Hub
    var activeHubTab by remember { mutableStateOf(0) }  // 0: Notes, 1: FlashcardsSpace, 2: Radar, 3: MindMap, 4: Profile

    val profile by viewModel.userProfile.collectAsState()
    val levelCelebration by viewModel.levelUpCelebration.collectAsState()
    val badgeCelebration by viewModel.unlockedBadgeCelebration.collectAsState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = activeBottomTab == 0,
                    onClick = { activeBottomTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        indicatorColor = primaryColor.copy(alpha = 0.12f)
                    )
                )

                NavigationBarItem(
                    selected = activeBottomTab == 1,
                    onClick = { activeBottomTab = 1 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Planner") },
                    label = { Text("Planner", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        indicatorColor = primaryColor.copy(alpha = 0.12f)
                    )
                )

                NavigationBarItem(
                    selected = activeBottomTab == 2,
                    onClick = { activeBottomTab = 2 },
                    icon = { Icon(Icons.Default.Timer, contentDescription = "Focus Timer") },
                    label = { Text("Timer", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        indicatorColor = primaryColor.copy(alpha = 0.12f)
                    )
                )

                NavigationBarItem(
                    selected = activeBottomTab == 3,
                    onClick = { activeBottomTab = 3 },
                    icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Aura Synth") },
                    label = { Text("Aura", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        indicatorColor = primaryColor.copy(alpha = 0.12f)
                    )
                )

                NavigationBarItem(
                    selected = activeBottomTab == 4,
                    onClick = { activeBottomTab = 4 },
                    icon = { Icon(Icons.Default.Window, contentDescription = "Hub Tools") },
                    label = { Text("Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        indicatorColor = primaryColor.copy(alpha = 0.12f)
                    )
                )
            }
        }
    ) { innerPadding ->
        // AnimatedContent switches screens smoothly
        AnimatedContent(
            targetState = activeBottomTab,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() with
                            slideOutHorizontally { width -> width } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { targetTab ->
            when (targetTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTimer = { activeBottomTab = 2 },
                    onNavigateToPlanner = { activeBottomTab = 1 },
                    onNavigateToNotes = {
                        activeBottomTab = 4
                        activeHubTab = 0
                    },
                    onNavigateToFlashcards = {
                        activeBottomTab = 4
                        activeHubTab = 1
                    }
                )
                1 -> PlannerScreen(viewModel = viewModel)
                2 -> TimerScreen(viewModel = viewModel)
                3 -> MusicScreen(viewModel = viewModel)
                4 -> HubShellScreen(
                    viewModel = viewModel,
                    activeHubTab = activeHubTab,
                    onHubTabChanged = { activeHubTab = it }
                )
                else -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTimer = { activeBottomTab = 2 },
                    onNavigateToPlanner = { activeBottomTab = 1 },
                    onNavigateToNotes = {},
                    onNavigateToFlashcards = {}
                )
            }
        }
    }

    // --- POP-UP GAMIFIED CELEBRATIONS ---

    // 1. Level up pop-up
    levelCelebration?.let { celebrationMsg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissCelebration() },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissCelebration() },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("UNLEASH VIBE POWER", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(secondaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Celebration",
                        tint = Color.Yellow,
                        modifier = Modifier.size(36.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "LEVEL ASCENSION",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = celebrationMsg,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // 2. Badge unlock pop-up
    badgeCelebration?.let { badgeMsg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissBadge() },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissBadge() },
                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor)
                ) {
                    Text("CLAIM THE REWARDS", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Style",
                        tint = primaryColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "THEME ENVELOPE OPENED!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = badgeMsg,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HubShellScreen(
    viewModel: VibeViewModel,
    activeHubTab: Int,
    onHubTabChanged: (Int) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Horizontally scrollable sub tab selector bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceColor)
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Vibe Notes", "Cards Active", "Rep Radar", "Mind Map", "Profile XP").forEachIndexed { index, title ->
                val active = activeHubTab == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (active) primaryColor else Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onHubTabChanged(index) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        color = if (active) primaryColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Horizontal line separator
        Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

        // Render selected subscreen with spring slide transition
        AnimatedContent(
            targetState = activeHubTab,
            transitionSpec = {
                slideInVertically { height -> height / 2 } + fadeIn() with
                        slideOutVertically { height -> height / 2 } + fadeOut()
            },
            modifier = Modifier.weight(1f),
            label = "HubTransition"
        ) { hubIndex ->
            when (hubIndex) {
                0 -> NotesScreen(viewModel = viewModel)
                1 -> FlashcardsScreen(viewModel = viewModel)
                2 -> RadarScreen(viewModel = viewModel)
                3 -> MindMapScreen(viewModel = viewModel)
                4 -> ProfileScreen(viewModel = viewModel)
                else -> NotesScreen(viewModel = viewModel)
            }
        }
    }
}
