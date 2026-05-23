package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VibeViewModel

@Composable
fun TimerScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsState()
    val focusMode by viewModel.focusMode.collectAsState()
    val timerStatus by viewModel.timerStatus.collectAsState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Format remaining time
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    // Calculation for progress circle
    val rawProgress = if (totalDurationSeconds == 0) 1f else timeRemaining.toFloat() / totalDurationSeconds
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
        label = "CircularProgress"
    )

    // Pulsing transition scale for running timer
    val transition = rememberInfiniteTransition(label = "TimerPulse")
    val pulseScale by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (timerStatus == "Running") 1.05f else 1.00f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulsing"
    )

    // Slow breath scaling guide
    val breathScale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathVisualizer"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // --- TITLE ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Focus Timer",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Calcribe your cognitive wavelength",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Timer Icon",
                    tint = primaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // --- FOCUS MODES TABS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceColor)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Pomodoro", "Short Break", "Long Break", "Free Focus").forEach { mode ->
                val active = focusMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (active) primaryColor else Color.Transparent
                        )
                        .clickable { viewModel.setFocusMode(mode) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode,
                        color = if (active) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // --- LARGE GLOWING PROGRESS TIMER COREL ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(260.dp)
        ) {
            // Background blur circles drawing
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(pulseScale)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            ),
                            radius = this.size.width / 1.1f
                        )
                    }
            )

            // Dynamic progress ring
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(220.dp),
                color = primaryColor,
                trackColor = primaryColor.copy(alpha = 0.09f),
                strokeWidth = 14.dp,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Inner core text display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = focusMode.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = primaryColor,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                )

                Text(
                    text = timeString,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 54.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = timerStatus,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // --- MANIPULATE TIMER BUTTONS (+5m / -5m) ---
        Row(
            modifier = Modifier.fillMaxWidth(0.6f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.adjustTimeRemaining(-5 * 60) },
                modifier = Modifier.background(surfaceColor, CircleShape)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Trim 5 minutes", tint = MaterialTheme.colorScheme.onBackground)
            }

            Text(
                text = "Adjust Duration",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            IconButton(
                onClick = { viewModel.adjustTimeRemaining(5 * 60) },
                modifier = Modifier.background(surfaceColor, CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add 5 minutes", tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        // --- CONTROLLER BUTTONS ---
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // STOP/RESET
            Button(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = surfaceColor,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Reset", fontWeight = FontWeight.Bold)
            }

            // PLAY/PAUSE FOR COGNITIVE WAVE
            Button(
                onClick = {
                    if (timerStatus == "Running") {
                        viewModel.pauseTimer()
                    } else {
                        viewModel.startTimer()
                    }
                },
                modifier = Modifier
                    .weight(1.5f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Icon(
                    imageVector = if (timerStatus == "Running") Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Trigger Focus",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (timerStatus == "Running") "Pause Vibe" else "Start Deep Vibe",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- BREATHING MEDITATION MODULE ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Expanding orb representing lungs
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(if (timerStatus == "Running") breathScale else 1.0f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        secondaryColor.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(secondaryColor)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Zen Breathing Beacon",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (timerStatus == "Running") {
                            "Sync your breath with the visual hub: Breathe in... Breathe out..."
                        } else {
                            "Unleash a focus session to run breathing pacers to isolate distractions."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
