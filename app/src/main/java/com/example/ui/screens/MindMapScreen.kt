package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MindMapNode
import com.example.ui.VibeViewModel
import kotlin.math.roundToInt

@Composable
fun MindMapScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val nodes by viewModel.mindMapNodes.collectAsState()

    var selectedNode by remember { mutableStateOf<MindMapNode?>(null) }
    var newNodeLabel by remember { mutableStateOf("") }
    var editNodeLabel by remember { mutableStateOf("") }
    var isEditingLabel by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Keep active selections synchronized with database state updates
    LaunchedEffect(nodes) {
        selectedNode = nodes.find { it.id == selectedNode?.id } ?: nodes.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    text = "Vibe Map",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Drag nodes & sprout intellectual branches",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            IconButton(
                onClick = { viewModel.resetMindMap() },
                modifier = Modifier.background(surfaceColor, CircleShape)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Clear Canvas", tint = primaryColor)
            }
        }

        // --- INTERACTIVE MIND MAP CANVAS CONTAINER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF07080F))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
        ) {
            // 1. Draw connection vectors in underlying background layer
            Canvas(modifier = Modifier.fillMaxSize()) {
                nodes.forEach { node ->
                    node.parentId?.let { pId ->
                        val parentNode = nodes.find { it.id == pId }
                        if (parentNode != null) {
                            // Bezier path connections for smooth organic curves!
                            val pStart = Offset(parentNode.x, parentNode.y)
                            val pEnd = Offset(node.x, node.y)

                            // Control coordinates
                            val cp1 = Offset(pStart.x, (pStart.y + pEnd.y) / 2)
                            val cp2 = Offset(pEnd.x, (pStart.y + pEnd.y) / 2)

                            val path = Path().apply {
                                moveTo(pStart.x, pStart.y)
                                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pEnd.x, pEnd.y)
                            }

                            drawPath(
                                path = path,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF5C7CFA).copy(alpha = 0.7f),
                                        Color(0xFFE599F7).copy(alpha = 0.7f)
                                    )
                                ),
                                style = Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }
                    }
                }
            }

            // 2. Render Node Bubbles which can be dragged around
            nodes.forEach { node ->
                val isSelected = selectedNode?.id == node.id
                val bubbleColor = if (node.parentId == null) primaryColor else secondaryColor
                val bubbleScale = if (node.parentId == null) 1.2f else 1.0f

                Box(
                    modifier = Modifier
                        .offset { IntOffset(node.x.roundToInt() - 65, node.y.roundToInt() - 25) }
                        .size(width = 130.dp, height = 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) bubbleColor else Color(0xFF141724)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color.White else bubbleColor.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedNode = node
                            editNodeLabel = node.label
                            isEditingLabel = false
                        }
                        .pointerInput(node.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                viewModel.updateNodePosition(
                                    node = node,
                                    newX = (node.x + dragAmount.x).coerceIn(40f, size.width.toFloat() - 40f),
                                    newY = (node.y + dragAmount.y).coerceIn(40f, size.height.toFloat() - 40f)
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = node.label,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }

            // Canvas Floating tip
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "👉 Drag nodes around with 1 finger!",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // --- CONTROL OPERATIONS PANEL (SPlit Pane level) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                selectedNode?.let { node ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Branch: ${node.label}",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleMedium,
                            color = primaryColor
                        )

                        // Trash node button
                        if (nodes.size > 1) {
                            IconButton(onClick = { viewModel.deleteNode(node) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Trash branch", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    // Branch sprouting input
                    OutlinedTextField(
                        value = newNodeLabel,
                        onValueChange = { newNodeLabel = it },
                        placeholder = { Text("Enter child stem label (e.g. Unit 3)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Share, contentDescription = "Branch")
                        }
                    )

                    Button(
                        onClick = {
                            if (newNodeLabel.isNotBlank()) {
                                // Sprout dynamic offset branch nearby parent node
                                val spawnX = node.x + ((-100..100).random().toFloat())
                                val spawnY = node.y + 120f
                                viewModel.addMindMapNode(
                                    label = newNodeLabel,
                                    parentId = node.id,
                                    x = spawnX.coerceAtLeast(60f),
                                    y = spawnY.coerceAtLeast(60f)
                                )
                                newNodeLabel = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sprout branch stemming here", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Click on any branch bubble above to synthesize stems.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
