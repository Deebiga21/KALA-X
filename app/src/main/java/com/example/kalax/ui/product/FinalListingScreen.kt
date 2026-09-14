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
fun FinalListingScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onPublish: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    var isPublishing by remember { mutableStateOf(false) }

    LaunchedEffect(isPublishing) {
        if (isPublishing) {
            delay(2000)
            onPublish()
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
            Text("Final Preview", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(draft.name.ifEmpty { "Product Name" }, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("₹${draft.recommendedPrice}", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text("${draft.category} • ${draft.material}", color = Color(0xFF0284C7), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(draft.description.ifEmpty { "Description here..." }, color = Color.DarkGray)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { isPublishing = true },
            modifier = Modifier.fillMaxWidth().padding(32.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
        ) {
            if (isPublishing) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
            } else {
                Text("Publish to Market", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
