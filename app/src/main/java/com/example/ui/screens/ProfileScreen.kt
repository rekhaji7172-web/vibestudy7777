package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.VibeViewModel

@Composable
fun ProfileScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val sessions by viewModel.sessions.collectAsState()

    var showEditName by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf("") }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface

    val unlockedList = profile.unlockedThemes.split(",")

    val completedTasksCount = tasks.count { it.isCompleted }
    val totalSessionsCount = sessions.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- GORGEOUS PROFILE CARD PANEL ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                secondaryColor.copy(alpha = 0.9f),
                                primaryColor.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Profile round glowing avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    if (showEditName) {
                        Row(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editNameInput,
                                onValueChange = { editNameInput = it },
                                placeholder = { Text("Enter nickname") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            IconButton(
                                onClick = {
                                    if (editNameInput.isNotBlank()) {
                                        viewModel.editUsername(editNameInput)
                                        showEditName = false
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White)
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = profile.username,
                                color = Color.White,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                )
                            )
                            IconButton(onClick = {
                                editNameInput = profile.username
                                showEditName = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit name",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Vibe Level ${profile.level}",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Badge(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ) {
                        Text("🔥 ${profile.streak} Days Streak", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- LEVEL UNLOCKED / REWARD THEMING DECK ---
        item {
            Text(
                text = "Milestone Theme Customizer",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    RewardTheme(
                        index = 0,
                        title = "Midnight Cosmic Slate 🌌",
                        desc = "Classical dark slate paired with deep electric sapphire nodes. (Unlocked)",
                        minLevel = 1,
                        priceXp = 0,
                        colors = listOf(Color(0xFF5C7CFA), Color(0xFF7048E8))
                    ),
                    RewardTheme(
                        index = 1,
                        title = "Teal Zen Garden 🌿",
                        desc = "Crisp mint details styled alongside calm dark forest emerald. (Level 2+)",
                        minLevel = 2,
                        priceXp = 50,
                        colors = listOf(Color(0xFF06D6A0), Color(0xFF118AB2))
                    ),
                    RewardTheme(
                        index = 2,
                        title = "Cyber-Pink Orchid Pulse 🔮",
                        desc = "Vibrant magenta highlights bound inside deep velvet voids. (Level 3+)",
                        minLevel = 3,
                        priceXp = 150,
                        colors = listOf(Color(0xFFF72585), Color(0xFF7209B7))
                    ),
                    RewardTheme(
                        index = 3,
                        title = "Sahara Sunset Oasis 🏜️",
                        desc = "Warm amber details wrapped comfortably inside clay charcoals. (Level 4+)",
                        minLevel = 4,
                        priceXp = 300,
                        colors = listOf(Color(0xFFFF9F1C), Color(0xFFE71D36))
                    )
                ).forEach { theme ->
                    val isLocked = !unlockedList.contains(theme.index.toString()) && profile.level < theme.minLevel
                    val isPurchasable = !unlockedList.contains(theme.index.toString()) && profile.level >= theme.minLevel
                    val isSelected = profile.selectedThemeIndex == theme.index
                    val isUnlocked = unlockedList.contains(theme.index.toString())

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) primaryColor else Color.White.copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Circular preview swatch
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(colors = theme.colors)
                                    )
                            )

                            // Title & Description
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = theme.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = theme.desc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }

                            // Selection/Lock Controls Button
                            when {
                                isLocked -> {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                isPurchasable -> {
                                    Button(
                                        onClick = { viewModel.purchaseTheme(theme.index, theme.priceXp) },
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                        modifier = Modifier.height(36.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Spend ${theme.priceXp} XP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                isUnlocked && !isSelected -> {
                                    TextButton(onClick = { viewModel.selectTheme(theme.index) }) {
                                        Text("Apply", fontWeight = FontWeight.Bold)
                                    }
                                }
                                isSelected -> {
                                    Badge(
                                        containerColor = primaryColor.copy(alpha = 0.15f),
                                        contentColor = primaryColor
                                    ) {
                                        Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- ACHIEVEMENT DECK ---
        item {
            Text(
                text = "Milestone Achievements",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    AchievementModel(
                        title = "Deep Work Pioneer 🧘",
                        desc = "Successfully trigger your first Pomodoro Focus session.",
                        badgeIcon = "🧘",
                        xpRewardLength = 100,
                        isComplete = totalSessionsCount >= 1
                    ),
                    AchievementModel(
                        title = "Streak Overlord 🔥",
                        desc = "Keep your consecutive active streak count above Day 3.",
                        badgeIcon = "🔥",
                        xpRewardLength = 300,
                        isComplete = profile.streak >= 3
                    ),
                    AchievementModel(
                        title = "Apex Planner 🚀",
                        desc = "Complete 5 distinct planner objectives in any category.",
                        badgeIcon = "🚀",
                        xpRewardLength = 250,
                        isComplete = completedTasksCount >= 5
                    ),
                    AchievementModel(
                        title = "Mind Weaver 🕸️",
                        desc = "Complete a study mindmap spanning 4 connected stems or branches.",
                        badgeIcon = "🕸️",
                        xpRewardLength = 200,
                        isComplete = viewModel.mindMapNodes.value.size >= 4
                    )
                ).forEach { ach ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (ach.isComplete) primaryColor.copy(alpha = 0.05f) else surfaceColor
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (ach.isComplete) primaryColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (ach.isComplete) primaryColor.copy(alpha = 0.15f) else surfaceColor.copy(alpha = 0.4f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ach.badgeIcon, fontSize = 22.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ach.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (ach.isComplete) primaryColor else MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = ach.desc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                if (ach.isComplete) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Achieved!",
                                        tint = Color(0xFF37B24D)
                                    )
                                    Text(
                                        "Earned",
                                        fontSize = 10.sp,
                                        color = Color(0xFF37B24D),
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        "+${ach.xpRewardLength} XP",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = secondaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class RewardTheme(
    val index: Int,
    val title: String,
    val desc: String,
    val minLevel: Int,
    val priceXp: Int,
    val colors: List<Color>
)

data class AchievementModel(
    val title: String,
    val desc: String,
    val badgeIcon: String,
    val xpRewardLength: Int,
    val isComplete: Boolean
)
