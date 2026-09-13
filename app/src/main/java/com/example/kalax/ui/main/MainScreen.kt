package com.example.kalax.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppState {
    INITIAL,
    PROCESSING_IMAGE,
    IMAGE_READY,
    RECORDING_AUDIO,
    TRANSLATING_AUDIO,
    CATALOG_READY,
    PUBLISHING,
    PUBLISHED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    var appState by remember { mutableStateOf(AppState.INITIAL) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(appState) {
        when (appState) {
            AppState.IMAGE_READY -> {
                delay(300)
                scrollState.animateScrollToItem(2)
            }
            AppState.CATALOG_READY -> {
                delay(500)
                scrollState.animateScrollToItem(6)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KALA-X", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    if (appState != AppState.INITIAL) {
                        IconButton(onClick = { appState = AppState.INITIAL }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Crossfade(
            targetState = appState == AppState.PUBLISHED,
            label = "publish_crossfade",
            animationSpec = tween(800)
        ) { isPublished ->
            if (isPublished) {
                PublishedCard(modifier = Modifier.padding(paddingValues).fillMaxSize())
            } else {
                LazyColumn(
                    state = scrollState,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
                    item {
                        Text(
                            text = "Create New Product",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Snap a photo and let AI do the rest.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        ImageCaptureSection(
                            appState = appState,
                            onCapture = {
                                if (appState == AppState.INITIAL) {
                                    coroutineScope.launch {
                                        appState = AppState.PROCESSING_IMAGE
                                        delay(2000)
                                        appState = AppState.IMAGE_READY
                                    }
                                }
                            }
                        )
                    }

                    item {
                        AnimatedVisibility(
                            visible = appState >= AppState.IMAGE_READY,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            AudioSection(
                                appState = appState,
                                onRecordClick = {
                                    if (appState == AppState.IMAGE_READY) {
                                        coroutineScope.launch {
                                            appState = AppState.RECORDING_AUDIO
                                            delay(3000)
                                            appState = AppState.TRANSLATING_AUDIO
                                            delay(2500)
                                            appState = AppState.CATALOG_READY
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = appState >= AppState.CATALOG_READY,
                            enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 100 }),
                            exit = fadeOut()
                        ) {
                            CatalogSection()
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = appState >= AppState.CATALOG_READY,
                            enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 150 }),
                            exit = fadeOut()
                        ) {
                            PricingSection()
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = appState >= AppState.CATALOG_READY,
                            enter = fadeIn(tween(1000)) + slideInVertically(initialOffsetY = { 200 }),
                            exit = fadeOut()
                        ) {
                            ScoreSection()
                        }
                    }
                    
                    item {
                        AnimatedVisibility(
                            visible = appState >= AppState.CATALOG_READY,
                            enter = fadeIn(tween(1200)) + slideInVertically(initialOffsetY = { 250 }),
                            exit = fadeOut()
                        ) {
                            PublishButton(
                                onPublish = {
                                    if (appState == AppState.CATALOG_READY) {
                                        coroutineScope.launch {
                                            appState = AppState.PUBLISHING
                                            delay(1500)
                                            appState = AppState.PUBLISHED
                                        }
                                    }
                                },
                                isPublishing = appState == AppState.PUBLISHING
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCaptureSection(appState: AppState, onCapture: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = appState == AppState.INITIAL, onClick = onCapture),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = when (appState) {
                AppState.INITIAL -> 0
                AppState.PROCESSING_IMAGE -> 1
                else -> 2
            },
            label = "image_state",
            animationSpec = tween(800)
        ) { state ->
            when (state) {
                0 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Tap shutter to capture raw product",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                1 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "AI analyzing & enhancing image...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White), // White background for enhanced look
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.FilterVintage, // Proxy for a beautiful vase
                                contentDescription = "Enhanced Product",
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    "✨ AI Enhanced",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
        
        if (appState == AppState.INITIAL) {
            FloatingActionButton(
                onClick = onCapture,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-24).dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Capture")
            }
        }
    }
}

@Composable
fun AudioSection(appState: AppState, onRecordClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Describe your product in your language",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "(Tamil, Hindi, etc.)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Crossfade(
                targetState = appState,
                label = "audio_state",
                animationSpec = tween(500)
            ) { state ->
                when (state) {
                    AppState.IMAGE_READY -> {
                        FilledIconButton(
                            onClick = onRecordClick,
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Record", modifier = Modifier.size(36.dp))
                        }
                    }
                    AppState.RECORDING_AUDIO -> {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(72.dp)
                        ) {
                            AudioWaveAnimation()
                        }
                    }
                    AppState.TRANSLATING_AUDIO -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "AI translating & formatting...",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                "Raw Audio:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "\"இது ஒரு அருமையான கையால் செய்யப்பட்ட களிமண் பானை...\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "✨ AI Polished Description:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "An exquisite, handcrafted terracotta vase. Perfect for elegant home decor and eco-friendly spaces.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioWaveAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_waves")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 5) {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 400, delayMillis = i * 100, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_$i"
            )
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height((40 * scale).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "AI Catalog Generation",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Product Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Handcrafted Terracotta Vase",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Text(
                    "Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Home Decor > Pottery",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Text(
                    "Keywords",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(onClick = {}, label = { Text("#handmade") })
                    AssistChip(onClick = {}, label = { Text("#terracotta") })
                    AssistChip(onClick = {}, label = { Text("#eco-friendly") })
                }
            }
        }
    }
}

@Composable
fun PricingSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Smart Pricing Calculator",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                PricingRow("Material", "₹250")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f))
                PricingRow("Labour", "₹300")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f))
                PricingRow("Packaging", "₹50")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "AI Market Analysis",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Market Range",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
                            )
                            Text(
                                "₹799 – ₹899",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Recommended Price",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "₹849",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PricingRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ScoreSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Commerce Readiness",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                var progress by remember { mutableStateOf(0f) }
                LaunchedEffect(Unit) {
                    animate(
                        initialValue = 0f,
                        targetValue = 0.91f,
                        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
                    ) { value, _ -> progress = value }
                }
                
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    "Ready to sell!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "91/100 Commerce Ready",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Tip",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Tip: Add product dimensions to reach 100%.",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun PublishButton(onPublish: () -> Unit, isPublishing: Boolean) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        ExtendedFloatingActionButton(
            onClick = onPublish,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            if (isPublishing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Publishing...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Storefront, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Publish to Store", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PublishedCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            var showConfetti by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300)
                showConfetti = true
            }
            
            AnimatedVisibility(
                visible = showConfetti,
                enter = scaleIn(tween(500, easing = OvershootInterpolator().toEasing())) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Successfully Published!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Final Product Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FilterVintage,
                            contentDescription = "Product Image",
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Handcrafted Terracotta Vase",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Home Decor > Pottery",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Text(
                                "₹849",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "An exquisite, handcrafted terracotta vase. Perfect for elegant home decor and eco-friendly spaces.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// Utility to wrap Android's OvershootInterpolator
class OvershootInterpolator(private val tension: Float = 2.0f) {
    fun getInterpolation(t: Float): Float {
        val t2 = t - 1.0f
        return t2 * t2 * ((tension + 1) * t2 + tension) + 1.0f
    }
    fun toEasing() = Easing { fraction -> getInterpolation(fraction) }
}
