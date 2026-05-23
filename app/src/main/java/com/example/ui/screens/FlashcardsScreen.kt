package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Flashcard
import com.example.ui.VibeViewModel

@Composable
fun FlashcardsScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val cards by viewModel.flashcards.collectAsState()

    var showAddCardForm by remember { mutableStateOf(false) }
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var cardCategory by remember { mutableStateOf("Physics") }

    var activeCardIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    // Smoothly animate the card rotation
    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CardFlipAngle"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- TITLE ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vibe Cards",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Active recall & spaced repetition cards",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                Button(
                    onClick = { showAddCardForm = !showAddCardForm },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (showAddCardForm) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "New Card",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (showAddCardForm) "Close" else "New Card", color = Color.White)
                }
            }
        }

        // --- DRAFT NEW CARD FORM ---
        item {
            AnimatedVisibility(
                visible = showAddCardForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "New Study Flashcard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            placeholder = { Text("Enter prompt / question...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = false
                        )

                        OutlinedTextField(
                            value = answerText,
                            onValueChange = { answerText = it },
                            placeholder = { Text("Enter prompt resolution / answer...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = false
                        )

                        OutlinedTextField(
                            value = cardCategory,
                            onValueChange = { cardCategory = it },
                            placeholder = { Text("Subject category (e.g. Physics)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (questionText.isNotBlank() && answerText.isNotBlank()) {
                                    viewModel.addFlashcard(questionText, answerText, cardCategory)
                                    questionText = ""
                                    answerText = ""
                                    showAddCardForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Deploy Flashcard", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- EMPTY STATE OR RENDER DECK ---
        if (cards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Style,
                                contentDescription = "Empty Stack",
                                tint = primaryColor.copy(alpha = 0.3f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "Revision Deck Empty",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Create custom study decks for Physics, Biology, or History to kick off active recall.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        } else {
            // Keep index bounds in check
            val sanitizedIndex = activeCardIndex.coerceIn(0, cards.size - 1)
            val currentCard = cards[sanitizedIndex]

            // 1. CAROUSEL PAGINATOR HEADER INDICATOR
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Card ${sanitizedIndex + 1} of ${cards.size}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = {
                                isFlipped = false
                                activeCardIndex = if (sanitizedIndex > 0) sanitizedIndex - 1 else cards.size - 1
                            },
                            modifier = Modifier.background(surfaceColor, CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Before")
                        }

                        IconButton(
                            onClick = {
                                isFlipped = false
                                activeCardIndex = if (sanitizedIndex < cards.size - 1) sanitizedIndex + 1 else 0
                            },
                            modifier = Modifier.background(surfaceColor, CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "After")
                        }
                    }
                }
            }

            // 2. STUNNING 3D ROTATIVE CORE CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .graphicsLayer {
                            this.rotationY = rotationY
                            this.cameraDistance = 12f * density
                        }
                        .clickable { isFlipped = !isFlipped },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped) secondaryColor.copy(alpha = 0.95f) else surfaceColor
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isFlipped) Color.Transparent else Color.White.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Card Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    // Mirror content if flipped past 90 degrees to hold straight text rendering
                                    if (rotationY > 90f) {
                                        this.rotationY = 180f
                                    }
                                },
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top Tag/Metadata Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Badge(
                                    containerColor = if (isFlipped) Color.White.copy(alpha = 0.2f) else primaryColor.copy(alpha = 0.2f),
                                    contentColor = if (isFlipped) Color.White else primaryColor
                                ) {
                                    Text(currentCard.category, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = if (isFlipped) "ANSWER MODE" else "PROMPT QUESTION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = if (isFlipped) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                )
                            }

                            // Middle Core Question or Answer Display
                            Text(
                                text = if (rotationY > 90f) currentCard.answer else currentCard.question,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = if (isFlipped) Color.White else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )

                            // Bottom Hint Prompt
                            Text(
                                text = if (isFlipped) "Tap card to recall question" else "Tap card to flip answer",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isFlipped) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }

            // 3. RESPONSIVE CONFIDENCE SCALES (Only shown when flipped, guiding recall levels!)
            item {
                AnimatedVisibility(
                    visible = statesReflectingFlip(rotationY > 90f),
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(
                            text = "How solid is your active recall?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Hard 🥵
                            ConfidenceButton(
                                label = "HARD 🥵",
                                color = Color(0xFFF03E3E),
                                modifier = Modifier.weight(1f)
                            ) {
                                viewModel.updateFlashcardConfidence(currentCard, 1)
                                advanceCard(cards.size, sanitizedIndex) { activeCardIndex = it }
                                isFlipped = false
                            }

                            // Good 👍
                            ConfidenceButton(
                                label = "GOOD 👌",
                                color = Color(0xFFF59F00),
                                modifier = Modifier.weight(1f)
                            ) {
                                viewModel.updateFlashcardConfidence(currentCard, 2)
                                advanceCard(cards.size, sanitizedIndex) { activeCardIndex = it }
                                isFlipped = false
                            }

                            // Mastered 🏆
                            ConfidenceButton(
                                label = "MASTERED 🥇",
                                color = Color(0xFF37B24D),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                viewModel.updateFlashcardConfidence(currentCard, 3)
                                advanceCard(cards.size, sanitizedIndex) { activeCardIndex = it }
                                isFlipped = false
                            }
                        }
                    }
                }
            }

            // 4. TRASH THE COMPONENT BUTTON
            item {
                Button(
                    onClick = {
                        viewModel.deleteFlashcard(currentCard)
                        isFlipped = false
                        activeCardIndex = if (sanitizedIndex > 0) sanitizedIndex - 1 else 0
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = surfaceColor,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Purge card")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Purge This Flashcard", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Simple state provider holding condition to mirror visual display
private fun statesReflectingFlip(flipped: Boolean): Boolean {
    return flipped
}

private fun advanceCard(size: Int, currentIndex: Int, setter: (Int) -> Unit) {
    if (size <= 1) return
    val next = if (currentIndex < size - 1) currentIndex + 1 else 0
    setter(next)
}

@Composable
fun ConfidenceButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(48.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
    }
}
