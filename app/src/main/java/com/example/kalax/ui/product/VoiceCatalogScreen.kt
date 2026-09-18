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

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

fun selectedLanguageLocale(lang: String): String {
    return when(lang) {
        "Tamil" -> "ta-IN"
        "Hindi" -> "hi-IN"
        else -> "en-US"
    }
}

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

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.get(0) ?: ""
            if (spokenText.isNotEmpty()) {
                state = "processing"
                viewModel.processSpeechText(spokenText)
            } else {
                state = "idle"
            }
        } else {
            state = "idle"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageLocale(selectedLanguage))
            }
            speechLauncher.launch(intent)
        } else {
            state = "idle"
        }
    }

    LaunchedEffect(pipelineState) {
        when (pipelineState) {
            is PipelineState.Transcribing, is PipelineState.Generating -> {
                localState = "processing"
                state = "processing"
            }
            is PipelineState.Idle -> {
                if (draft.name.isNotEmpty() || draft.description.isNotEmpty() || draft.transcribedText.isNotEmpty()) {
                    localState = "done"
                    state = "done"
                }
            }
            else -> {}
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
            Column {
                Text("Describe your product", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Speak naturally in your language.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
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
                    label = { Text(lang, color = if (selectedLanguage == lang) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
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
                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                    },
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(Icons.Outlined.Mic, contentDescription = null, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Tap to start recording", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = { state = "text_entry" }) {
                    Text("Or type manually", color = MaterialTheme.colorScheme.primary)
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
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(24.dp))
                Text("AI is generating your catalog...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (state == "text_entry") {
                OutlinedTextField(
                    value = fallbackText,
                    onValueChange = { fallbackText = it },
                    label = { Text("Product Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Generate Catalog", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            } else {
                // Done state - AI Catalog Generation Review
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("AI Generated Catalog", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(Icons.Outlined.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = draft.name,
                            onValueChange = { val newName = it; viewModel.updateDraft { it.copy(name = newName) } },
                            label = { Text("Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground, unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = draft.description,
                            onValueChange = { val newDesc = it; viewModel.updateDraft { it.copy(description = newDesc) } },
                            label = { Text("Description") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground, unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Next", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
