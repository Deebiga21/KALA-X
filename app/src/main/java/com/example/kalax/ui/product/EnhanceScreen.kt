package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
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
fun EnhanceScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var isDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000)
        isDone = true
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
                Text("AI Enhancement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Making your product market-ready", color = Color.Gray, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isDone) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Enhancement Complete", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            } else {
                CircularProgressIndicator(color = Color(0xFF06B6D4))
                Spacer(modifier = Modifier.height(24.dp))
                Text("AI is preparing your product...", color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onNext,
                enabled = isDone,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
            ) {
                Text("Use Enhanced Image", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
