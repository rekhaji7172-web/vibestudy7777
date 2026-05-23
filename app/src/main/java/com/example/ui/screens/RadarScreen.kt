package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VibeViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val cards by viewModel.flashcards.collectAsState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Continuously rotating scanning sweep angle
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Sweep"
    )

    // Pulse animation for radar nodes
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    // Subdividing cards into spaced categories
    val urgentRecallList = cards.filter { it.confidence <= 1 }
    val stableList = cards.filter { it.confidence == 2 }
    val masteredList = cards.filter { it.confidence == 3 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- HEADER ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Revision Radar",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Spaced repetition tracking scan",
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
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // --- RADAR CANVAS SWEEP COMPONENT ---
        item {
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
                        text = "ACTIVE SCANNING SWEEP",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = primaryColor,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )

                    // Draw our dynamic circular radar scan on canvas
                    Canvas(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Transparent)
                    ) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radiusMax = size.width / 2

                        // 1. Draw concentrical rings
                        drawCircle(center = center, radius = radiusMax, color = primaryColor.copy(alpha = 0.1f), style = Stroke(1.dp.toPx()))
                        drawCircle(center = center, radius = radiusMax * 0.67f, color = primaryColor.copy(alpha = 0.08f), style = Stroke(1.dp.toPx()))
                        drawCircle(center = center, radius = radiusMax * 0.33f, color = primaryColor.copy(alpha = 0.05f), style = Stroke(1.dp.toPx()))

                        // 2. Draw cross hairs
                        drawLine(color = primaryColor.copy(alpha = 0.1f), start = Offset(0f, size.height / 2), end = Offset(size.width, size.height / 2))
                        drawLine(color = primaryColor.copy(alpha = 0.1f), start = Offset(size.width / 2, 0f), end = Offset(size.width / 2, size.height))

                        // 3. Draw scanning sweep line
                        val angleRad = Math.toRadians(sweepAngle.toDouble())
                        val sweepEndX = center.x + radiusMax * cos(angleRad).toFloat()
                        val sweepEndY = center.y + radiusMax * sin(angleRad).toFloat()

                        // Drawing sweep line
                        drawLine(
                            color = primaryColor,
                            start = center,
                            end = Offset(sweepEndX, sweepEndY),
                            strokeWidth = 3f
                        )

                        // Draw scanning shaded gradient fan
                        // We achieve a nice look by putting multiple slightly translucent lines preceding it
                        for (i in 0..15) {
                            val prevAngleRad = Math.toRadians((sweepAngle - i * 2).toDouble())
                            val pX = center.x + radiusMax * cos(prevAngleRad).toFloat()
                            val pY = center.y + radiusMax * sin(prevAngleRad).toFloat()
                            drawLine(
                                color = primaryColor.copy(alpha = 0.15f * (1f - (i.toFloat() / 15f))),
                                start = center,
                                end = Offset(pX, pY),
                                strokeWidth = 2f
                            )
                        }

                        // 4. Draw simulated blinking blips representing cards/subjects
                        // Angle placements
                        val blipsCoordinates = listOf(
                            Pair(center.x - radiusMax * 0.4f, center.y - radiusMax * 0.3f) to Color(0xFFF03E3E), // Red (urgent)
                            Pair(center.x + radiusMax * 0.5f, center.y - radiusMax * 0.5f) to Color(0xFFF59F00), // Orange (stable)
                            Pair(center.x - radiusMax * 0.5f, center.y + radiusMax * 0.4f) to Color(0xFF37B24D), // Green (mastered)
                            Pair(center.x + radiusMax * 0.2f, center.y + radiusMax * 0.3f) to Color(0xFFF03E3E)  // Red
                        )

                        blipsCoordinates.forEach { (coord, color) ->
                            drawCircle(
                                color = color.copy(alpha = pulseAlpha),
                                radius = 7.dp.toPx(),
                                center = Offset(coord.first, coord.second)
                            )
                            drawCircle(
                                color = color,
                                radius = 4.dp.toPx(),
                                center = Offset(coord.first, coord.second)
                            )
                        }
                    }

                    Text(
                        text = "System active. ${cards.size} study tracking coordinates detected.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // --- ALERTS DECK CARDS ---
        item {
            Text(
                text = "Spaced Recall Logs",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // URGENT ITEMS
        item {
            RevisionUrgencyHeader("CRITICAL SCAN: ACTION NEEDED ASAP", Color(0xFFF03E3E), urgentRecallList.size)
        }

        if (urgentRecallList.isEmpty()) {
            item { EmptyLogCard("Urgent space sterilized. No revision items pending.") }
        } else {
            items(urgentRecallList) { card ->
                RevisionTrackingCard(card.category, card.question, "NEEDS ACTIVE REPLAY", Color(0xFFF03E3E), surfaceColor)
            }
        }

        // STABLE STATE
        item {
            RevisionUrgencyHeader("STABLE QUEUE: NORMAL CYCLE", Color(0xFFF59F00), stableList.size)
        }

        if (stableList.isEmpty()) {
            item { EmptyLogCard("No items in warm review schedule.") }
        } else {
            items(stableList) { card ->
                RevisionTrackingCard(card.category, card.question, "STABLE QUEUED", Color(0xFFF59F00), surfaceColor)
            }
        }

        // DEEPLY MASTERED
        item {
            RevisionUrgencyHeader("STERILIZED SPACE: MASTERED", Color(0xFF37B24D), masteredList.size)
        }

        if (masteredList.isEmpty()) {
            item { EmptyLogCard("No topics fully mastered yet. Drill flashcards!") }
        } else {
            items(masteredList) { card ->
                RevisionTrackingCard(card.category, card.question, "STERILIZED / MASTERED", Color(0xFF37B24D), surfaceColor)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RevisionUrgencyHeader(title: String, color: Color, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = color,
                fontSize = 11.sp
            )
        }
        Text(
            text = "$count objects",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun RevisionTrackingCard(
    category: String,
    prompt: String,
    tag: String,
    themeColor: Color,
    containerColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(themeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.take(1).uppercase(),
                    color = themeColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    fontSize = 14.sp
                )
                Text(
                    text = "Category: $category",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Badge(
                containerColor = themeColor.copy(alpha = 0.15f),
                contentColor = themeColor
            ) {
                Text(tag, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmptyLogCard(prompt: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = prompt,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp
            )
        }
    }
}
