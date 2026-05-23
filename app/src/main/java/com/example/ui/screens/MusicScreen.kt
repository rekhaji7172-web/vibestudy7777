package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VibeViewModel

@Composable
fun MusicScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val activeSynthMode by viewModel.activeSynthMode.collectAsState()
    val synthVolume by viewModel.synthVolume.collectAsState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface

    val transition = rememberInfiniteTransition(label = "Waveform")
    
    // Waveform heights animations
    val barAnimators = List(12) { index ->
        transition.animateFloat(
            initialValue = 0.15f,
            targetValue = if (activeSynthMode != "None") {
                0.3f + (0.7f * (index % 3 + 1).toFloat() / 3f)
            } else {
                0.15f
            },
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 45),
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Bar$index"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Aura Synthesizer",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Isolate audio pollution with acoustic bio-hacks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(secondaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MusicNote,
                    contentDescription = "Music",
                    tint = secondaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // --- ANIMATED WAVEFORM VISUAL COMPONENT ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (activeSynthMode != "None") "CURRENT WAVELENGTH ACTIVE" else "STUDY FREQUENCY COMPOSER OFF",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (activeSynthMode != "None") secondaryColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )

                // Visualizer panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    barAnimators.forEach { anim ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(anim.value)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor,
                                            secondaryColor
                                        )
                                    )
                                )
                        )
                    }
                }

                Text(
                    text = when (activeSynthMode) {
                        "Binaural" -> "📡isPlaying 40Hz Brainwave Multiplier"
                        "WhiteNoise" -> "🌫️isPlaying Static Cosmic White Isolation"
                        "Rain" -> "🌿isPlaying Calming Zen Rain Canopy"
                        else -> "Silence Mode"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeDown,
                        contentDescription = "Volume Down",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Slider(
                        value = synthVolume,
                        onValueChange = { viewModel.setMusicVolume(it) },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = secondaryColor,
                            activeTrackColor = secondaryColor.copy(alpha = 0.8f),
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume Up",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // --- SELECTION CARDS ---
        Text(
            text = "Synthesizer Algorithms",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            listOf(
                SynthModel(
                    modeId = "Binaural",
                    title = "40Hz Gamma Binaural Beats",
                    desc = "Plays slightly offset frequencies in individual ears (200Hz L / 240Hz R) to recruit attention networks in the cerebrum.",
                    icon = Icons.Default.NetworkCell,
                    color = Color(0xFF5C7CFA)
                ),
                SynthModel(
                    modeId = "WhiteNoise",
                    title = "Cosmic Static White Noise",
                    desc = "Flat, uniform acoustic signal designed to cancel out transient environmental clatter and noisy spaces.",
                    icon = Icons.Default.FilterFrames,
                    color = Color(0xFFA5F3FC)
                ),
                SynthModel(
                    modeId = "Rain",
                    title = "Zen Rain Canopy Synthesizer",
                    desc = "Procedural, random droplet sizzles and water rushes formulated to activate default-mode peace networks.",
                    icon = Icons.Default.WaterDrop,
                    color = Color(0xFF20C997)
                )
            ).forEach { model ->
                val active = activeSynthMode == model.modeId
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleSound(model.modeId) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (active) model.color.copy(alpha = 0.1f) else surfaceColor
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (active) model.color else Color.White.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(model.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = model.icon,
                                contentDescription = model.title,
                                tint = model.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = model.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = model.desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }

                        // Play indicator icon
                        Icon(
                            imageVector = if (active) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Control",
                            tint = if (active) model.color else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

data class SynthModel(
    val modeId: String,
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
