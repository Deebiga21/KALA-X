package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ReadinessScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    var isCalculating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.getCommerceScore()
        isCalculating = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5E1))
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF4A5D44))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Commerce Readiness", color = Color(0xFF4A5D44), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Optimize for success", color = Color(0xFF697A63), fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCalculating) {
                Spacer(modifier = Modifier.height(64.dp))
                CircularProgressIndicator(color = Color(0xFF98B891), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("Analyzing your product listing...", color = Color(0xFF697A63))
            } else {
                // Score Ring
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                    CircularProgressIndicator(
                        progress = { draft.score / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF98B891),
                        trackColor = Color(0xFFC4D1A4),
                        strokeWidth = 12.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${draft.score}", color = Color(0xFF4A5D44), fontSize = 48.sp, fontWeight = FontWeight.Bold)
                        Text("out of 100", color = Color(0xFF697A63), fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Score Breakdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Score Breakdown", color = Color(0xFF4A5D44), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    BreakdownRow("Product Image", if (draft.enhancedImageUri != null) 95 else 40)
                    BreakdownRow("Description", if (draft.description.isNotEmpty()) 88 else 30)
                    BreakdownRow("Pricing", if (draft.recommendedPrice > 0) 92 else 10)
                    BreakdownRow("Category", if (draft.category.isNotEmpty()) 91 else 10)
                    BreakdownRow("Completeness", if (draft.dimensions.isNotEmpty()) 100 else 76)
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                if (draft.dimensions.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(0.1f)),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(0.3f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Product dimensions missing", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Add dimensions to reach 95+", color = Color(0xFF4A5D44), fontSize = 12.sp)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { 
                                    viewModel.updateDraft { it.copy(dimensions = "12x8x6") }
                                    viewModel.getCommerceScore()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Add Dimensions", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(0.1f)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Ready for Market! 🎉", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))

        if (!isCalculating) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = onNext,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("Skip", color = Color(0xFF4A5D44))
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1.5f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98B891))
                ) {
                    Text("Complete Product", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BreakdownRow(label: String, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF697A63), fontSize = 12.sp, modifier = Modifier.weight(1f))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.weight(1.5f).height(6.dp),
            color = Color(0xFF98B891),
            trackColor = Color(0xFFC4D1A4)
        )
        Text("$score", color = Color(0xFF4A5D44), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp).padding(start = 8.dp))
    }
}
