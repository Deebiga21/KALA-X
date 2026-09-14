package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun PricingScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    var isCalculating by remember { mutableStateOf(false) }

    LaunchedEffect(isCalculating) {
        if (isCalculating) {
            delay(1500)
            viewModel.updateDraft { 
                it.copy(
                    rawCost = 300,
                    labourCost = 200,
                    packagingCost = 50,
                    otherCost = 50,
                    totalCost = 600,
                    recommendedPrice = 849
                )
            }
            isCalculating = false
        }
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
                Text("Smart Pricing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Find a fair and competitive price", color = Color.Gray, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Text("Input Costs (₹)", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (draft.recommendedPrice == 0) {
                Button(
                    onClick = { isCalculating = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6).copy(0.2f))
                ) {
                    if (isCalculating) {
                        CircularProgressIndicator(color = Color(0xFF3B82F6), modifier = Modifier.size(24.dp))
                    } else {
                        Text("Generate AI Price", color = Color(0xFF3B82F6))
                    }
                }
            } else {
                Text("Recommended Price", color = Color.Gray)
                Text("₹${draft.recommendedPrice}", color = Color(0xFF06B6D4), fontSize = 48.sp, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
                ) {
                    Text("Accept ₹${draft.recommendedPrice}", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
