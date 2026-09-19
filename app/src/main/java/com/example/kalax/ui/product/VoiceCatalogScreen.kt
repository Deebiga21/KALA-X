package com.example.kalax.ui.product

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import java.io.File

fun selectedLanguageLocale(lang: String): String {
    return when(lang) {
        "Tamil" -> "ta-IN"
        "Hindi" -> "hi-IN"
        else -> "en-US"
    }
}

// ─── AI Pipeline Step Model ──────────────────────────────────────────

data class AiPipelineStep(
    val label: String,
    val description: String
)

private val aiPipelineSteps = listOf(
    AiPipelineStep("Analyzing Speech", "Parsing your voice input..."),
    AiPipelineStep("Understanding Context", "Identifying product type & materials..."),
    AiPipelineStep("Generating Title", "Crafting a market-ready product name..."),
    AiPipelineStep("Writing Description", "Creating a compelling listing description..."),
    AiPipelineStep("Tagging Keywords", "Adding SEO-optimized search tags..."),
    AiPipelineStep("Finalizing Catalog", "Polishing and reviewing the output...")
)

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
    
    // Progress tracking
    var currentStep by remember { mutableIntStateOf(0) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var processingJob by remember { mutableStateOf<Job?>(null) }
    var isStuck by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.get(0) ?: ""
            if (spokenText.isNotEmpty()) {
                state = "processing"
                currentStep = 0
                elapsedSeconds = 0
                isStuck = false
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

    // Simulate step progression while processing
    LaunchedEffect(state) {
        if (state == "processing") {
            processingJob = scope.launch {
                while (state == "processing") {
                    delay(1000)
                    elapsedSeconds++

                    // Advance steps based on elapsed time (simulated progression)
                    val newStep = when {
                        elapsedSeconds < 2 -> 0
                        elapsedSeconds < 4 -> 1
                        elapsedSeconds < 6 -> 2
                        elapsedSeconds < 8 -> 3
                        elapsedSeconds < 10 -> 4
                        else -> 5
                    }
                    if (newStep > currentStep) currentStep = newStep

                    // Mark as potentially stuck after 30 seconds
                    if (elapsedSeconds >= 30) {
                        isStuck = true
                    }
                }
            }
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
                    processingJob?.cancel()
                }
            }
            else -> {}
        }
    }

    // Cancel handler
    fun cancelProcessing() {
        processingJob?.cancel()
        state = "idle"
        currentStep = 0
        elapsedSeconds = 0
        isStuck = false
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
            IconButton(onClick = {
                if (state == "processing") cancelProcessing()
                onBack()
            }) {
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
                // ─── Real-Time AI Pipeline Progress ──────────────────────
                val context = LocalContext.current
                AiProcessingView(
                    currentStep = currentStep,
                    totalSteps = aiPipelineSteps.size,
                    elapsedSeconds = elapsedSeconds,
                    isStuck = isStuck,
                    context = context,
                    onCancel = { cancelProcessing() }
                )

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
                        currentStep = 0
                        elapsedSeconds = 0
                        isStuck = false
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

// ─── AI Processing Progress View ─────────────────────────────────────

@Composable
fun AiProcessingView(
    currentStep: Int,
    totalSteps: Int,
    elapsedSeconds: Int,
    isStuck: Boolean,
    context: Context,
    onCancel: () -> Unit
) {
    val progress = (currentStep + 1).toFloat() / totalSteps.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // Hardware stats
    var currentTemp by remember { mutableFloatStateOf(0f) }
    var ramUsageGb by remember { mutableFloatStateOf(0f) }
    var totalRamGb by remember { mutableFloatStateOf(0f) }
    
    // Poll hardware metrics
    LaunchedEffect(elapsedSeconds) {
        // Temperature (from BatteryManager as a reliable proxy on most devices)
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val tempValue = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        currentTemp = tempValue / 10f
        
        // RAM Usage
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val totalBytes = memoryInfo.totalMem
        val availBytes = memoryInfo.availMem
        val usedBytes = totalBytes - availBytes
        
        ramUsageGb = usedBytes / (1024f * 1024f * 1024f)
        totalRamGb = totalBytes / (1024f * 1024f * 1024f)
    }

    // Pulsing dot animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // New LatticeLoader (matches React component)
            com.example.kalax.ui.components.LatticeLoader(
                status = if (isStuck) "error" else "working",
                label = "AI is working",
                elapsedSeconds = elapsedSeconds,
                activeColor = MaterialTheme.colorScheme.primary,
                errorColor = Color(0xFFF59E0B) // Amber for stuck
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Hardware Stats Row
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Temp
                val tempColor = when {
                    currentTemp > 42f -> Color(0xFFE53935)
                    currentTemp > 38f -> Color(0xFFF59E0B)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Text(
                    text = "🌡 %.1f°C".format(currentTemp),
                    fontSize = 11.sp,
                    color = tempColor
                )
                
                Text(
                    text = " • ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.3f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                // RAM
                Text(
                    text = "🧠 %.1f/%.1f GB".format(ramUsageGb, totalRamGb),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isStuck) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Step ${currentStep + 1} of $totalSteps",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step list
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                aiPipelineSteps.forEachIndexed { index, step ->
                    val isActive = index == currentStep
                    val isComplete = index < currentStep
                    val isFuture = index > currentStep

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Status indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = when {
                                        isComplete -> MaterialTheme.colorScheme.primary
                                        isActive -> MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = step.label,
                                fontSize = 13.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isComplete -> MaterialTheme.colorScheme.primary
                                    isActive -> MaterialTheme.colorScheme.onBackground
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                                }
                            )
                            if (isActive) {
                                Text(
                                    text = step.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Stuck warning
            AnimatedVisibility(visible = isStuck) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "⚠ This is taking longer than expected.\nThe AI model may be loading for the first time.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cancel button — always visible
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isStuck) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f)
                )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isStuck) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isStuck) "Cancel & Try Again" else "Cancel",
                    color = if (isStuck) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isStuck) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}
