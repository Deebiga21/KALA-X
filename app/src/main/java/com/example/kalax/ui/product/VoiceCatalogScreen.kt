package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Mic
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
fun VoiceCatalogScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var state by remember { mutableStateOf("idle") } // idle, recording, processing, done

    LaunchedEffect(state) {
        if (state == "recording") {
            delay(2000)
            state = "processing"
            delay(2000)
            viewModel.updateDraft { 
                it.copy(
                    name = "Handmade Bamboo Basket",
                    category = "Home & Lifestyle",
                    material = "Natural Bamboo",
                    description = "A beautifully handcrafted bamboo basket made using traditional weaving techniques."
                )
            }
            state = "done"
        }
    }

    val draft by viewModel.draft.collectAsState()

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
                Text("Describe Product", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Speak naturally in your language", color = Color.Gray, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state == "idle") {
                FloatingActionButton(
                    onClick = { state = "recording" },
                    containerColor = Color(0xFF06B6D4),
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(Icons.Outlined.Mic, contentDescription = null, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Tap microphone to start", color = Color.Gray)
            } else if (state == "recording") {
                FloatingActionButton(
                    onClick = {  },
                    containerColor = Color.Red.copy(0.5f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(Icons.Outlined.Mic, contentDescription = null, tint = Color.Red, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Recording...", color = Color.Red)
            } else if (state == "processing") {
                CircularProgressIndicator(color = Color(0xFF06B6D4))
                Spacer(modifier = Modifier.height(24.dp))
                Text("Understanding voice...", color = Color.Gray)
            } else {
                // Done state
                Text("AI Generated Catalog", color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = { val newName = it; viewModel.updateDraft { it.copy(name = newName) } },
                    label = { Text("Product Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF06B6D4),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
                ) {
                    Text("Continue to Pricing", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
