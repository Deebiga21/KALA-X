package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    
    LaunchedEffect(Unit) {
        viewModel.updateDraft { it.copy(score = 86) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Commerce Readiness", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Ensure your product is ready", color = Color.Gray, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("${draft.score}", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Bold)
            Text("Out of 100", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (draft.dimensions.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Missing Information", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                        }
                        Text("Add product dimensions to boost score.", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.updateDraft { it.copy(dimensions = "12x8x6", score = 96) } }) {
                            Text("Add Dimensions")
                        }
                    }
                }
            } else {
                Text("Ready to Publish! 🎉", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
            ) {
                Text("Review Final Listing", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
