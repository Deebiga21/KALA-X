package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCatalogScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var state by remember { mutableStateOf("idle") } // idle, recording, processing, text_entry, done
    val draft by viewModel.draft.collectAsState()
    val pipelineState by viewModel.pipelineState.collectAsState()
    
    var localState by remember { mutableStateOf("idle") }
    var fallbackText by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("English") }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(pipelineState) {
        when (pipelineState) {
            "ProcessingImage", "TranscribingAudio", "GeneratingCatalog" -> {
                localState = "processing"
                state = "processing"
            }
            "CatalogGenerated" -> {
                localState = "done"
                state = "done"
            }
        }
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
                Text("Describe your product", color = Color(0xFF4A5D44), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Speak naturally in your language.", color = Color(0xFF697A63), fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Language Selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Tamil", "Hindi", "English").forEach { lang ->
                FilterChip(
                    selected = selectedLanguage == lang,
                    onClick = { selectedLanguage = lang },
                    label = { Text(lang, color = if (selectedLanguage == lang) Color(0xFF4A5D44) else Color(0xFF697A63)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF98B891).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF98B891)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state == "idle") {
                FloatingActionButton(
                    onClick = {
                        state = "recording"
                        scope.launch {
                            viewModel.processVoice(selectedLanguage)
                            viewModel.generateCatalog()
                        }
                    },
                    containerColor = Color(0xFF98B891).copy(alpha = 0.2f),
                    contentColor = Color(0xFF98B891),
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(Icons.Outlined.Mic, contentDescription = null, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Tap to start recording", color = Color(0xFF697A63), fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = { state = "text_entry" }) {
                    Text("Or type manually", color = Color(0xFF98B891))
                }
            } else if (state == "recording") {
                FloatingActionButton(
                    onClick = { /* Stop handled implicitly in this demo flow */ },
                    containerColor = Color.Red.copy(0.2f),
                    contentColor = Color.Red,
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(Icons.Outlined.Mic, contentDescription = null, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Listening...", color = Color.Red)
            } else if (state == "processing") {
                CircularProgressIndicator(color = Color(0xFF98B891))
                Spacer(modifier = Modifier.height(24.dp))
                Text("AI is generating your catalog...", color = Color(0xFF697A63))
            } else if (state == "text_entry") {
                OutlinedTextField(
                    value = fallbackText,
                    onValueChange = { fallbackText = it },
                    label = { Text("Product Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF4A5D44),
                        unfocusedTextColor = Color(0xFF4A5D44),
                        focusedLabelColor = Color(0xFF98B891),
                        unfocusedLabelColor = Color(0xFF697A63),
                        focusedBorderColor = Color(0xFF98B891),
                        unfocusedBorderColor = Color(0xFF697A63)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        state = "processing"
                        viewModel.updateDraft { it.copy(transcribedText = fallbackText) }
                        viewModel.generateCatalog()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98B891))
                ) {
                    Text("Generate Catalog", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                // Done state - AI Catalog Generation Review
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD5E0B5)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("AI Generated Catalog", color = Color(0xFF98B891), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color(0xFF697A63), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = draft.name,
                            onValueChange = { val newName = it; viewModel.updateDraft { it.copy(name = newName) } },
                            label = { Text("Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF4A5D44), unfocusedTextColor = Color(0xFF4A5D44),
                                focusedBorderColor = Color(0xFF98B891), unfocusedBorderColor = Color(0xFFC4D1A4)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = draft.description,
                            onValueChange = { val newDesc = it; viewModel.updateDraft { it.copy(description = newDesc) } },
                            label = { Text("Description") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF4A5D44), unfocusedTextColor = Color(0xFF4A5D44),
                                focusedBorderColor = Color(0xFF98B891), unfocusedBorderColor = Color(0xFFC4D1A4)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98B891))
                ) {
                    Text("Next", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
