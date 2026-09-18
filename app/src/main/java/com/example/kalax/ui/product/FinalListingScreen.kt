package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
            delay(1500)
            viewModel.publishDraft()
            onPublish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Final Preview", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.onSurfaceVariant)) {
                    val displayImage = draft.enhancedImageUri ?: draft.imageUri
                    if (displayImage != null) {
                        AsyncImage(
                            model = java.io.File(displayImage),
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(draft.name.ifEmpty { "Product Name" }, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("₹${draft.recommendedPrice}", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text("${draft.category} • ${draft.material}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(draft.description.ifEmpty { "Description here..." }, color = Color.DarkGray, fontSize = 14.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { 
                    viewModel.saveDraft()
                    onPublish() // We route them back to home/catalog anyway
                },
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Save Draft", color = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { isPublishing = true },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Publish Now", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
