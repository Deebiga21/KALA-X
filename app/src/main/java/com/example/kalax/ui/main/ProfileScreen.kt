package com.example.kalax.ui.main

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalax.ui.home.BottomNavBar
import com.example.kalax.ui.product.ProductViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: ProductViewModel
) {
    val catalog by viewModel.catalog.collectAsState()
    val published = catalog.count { it.status == "Published" }

    val cardBg = MaterialTheme.colorScheme.surface
    val cyanGlow = MaterialTheme.colorScheme.primary

    val profileState by viewModel.profile.collectAsState()
    val artisanName = profileState?.name ?: "Artisan"
    val preferredLanguage = profileState?.preferredLanguage ?: "English"

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(artisanName) { mutableStateOf(artisanName) }
    var editLanguage by remember(preferredLanguage) { mutableStateOf(preferredLanguage) }

    // Sheet states for clickable settings
    var showPrivacySheet by remember { mutableStateOf(false) }
    var showHelpSheet by remember { mutableStateOf(false) }
    var showAboutSheet by remember { mutableStateOf(false) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editLanguage,
                        onValueChange = { editLanguage = it },
                        label = { Text("Preferred Language") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateProfile(editName, editLanguage)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- Privacy Bottom Sheet ---
    if (showPrivacySheet) {
        SettingsBottomSheet(
            title = "Privacy Policy",
            onDismiss = { showPrivacySheet = false }
        ) {
            PrivacyContent()
        }
    }

    // --- Help & Support Bottom Sheet ---
    if (showHelpSheet) {
        SettingsBottomSheet(
            title = "Help & Support",
            onDismiss = { showHelpSheet = false }
        ) {
            HelpContent()
        }
    }

    // --- About KALA-X Bottom Sheet ---
    if (showAboutSheet) {
        SettingsBottomSheet(
            title = "About KALA-X",
            onDismiss = { showAboutSheet = false }
        ) {
            AboutContent()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = cyanGlow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("My Profile", color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text("Your artisan business", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    // Profile Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(artisanName, color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text("Handicraft Artisan", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tamil Nadu, India", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(preferredLanguage, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.align(Alignment.End).height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text("Edit Profile", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Profile Completion
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Profile Completion", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                            Text("82%", color = cyanGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.82f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = cyanGlow,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stats
                    Text("My Business", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ProfileStatBox(modifier = Modifier.weight(1f), Icons.Outlined.Inventory2, "${catalog.size}", "Products")
                            ProfileStatBox(modifier = Modifier.weight(1f), Icons.Outlined.CheckCircle, "$published", "Published")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ProfileStatBox(modifier = Modifier.weight(1f), Icons.Outlined.ShoppingCart, "₹24.6K", "Total Sales")
                            ProfileStatBox(modifier = Modifier.weight(1f), Icons.Outlined.StarOutline, "91/100", "Commerce Score")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Settings
                    Text("Settings", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingItem(Icons.Default.Language, "Language", preferredLanguage, onClick = {})
                    SettingToggleItem(Icons.Default.Notifications, "Notifications", true)
                    SettingItem(Icons.Default.Lock, "Privacy", null, onClick = { showPrivacySheet = true })
                    SettingItem(Icons.Default.Info, "Help & Support", null, onClick = { showHelpSheet = true })
                    SettingItem(Icons.Default.CheckCircle, "About KALA-X", null, onClick = { showAboutSheet = true })
                    
                    Spacer(modifier = Modifier.height(100.dp)) // Nav padding
                }
            }
        }
        
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            currentRoute = currentRoute,
            onNavigate = onNavigate
        )
    }
}

// ─── Bottom Sheet Wrapper ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(0.05f))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            content()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Privacy Content ─────────────────────────────────────────────────

@Composable
fun ColumnScope.PrivacyContent() {
    val sections = listOf(
        "On-Device Processing" to "KALA-X processes your product photos and voice descriptions entirely on your device using edge AI. Your raw images and audio never leave your phone.",
        "Data Collection" to "We collect only the minimal data needed to improve your experience:\n• Product listings you choose to publish\n• Pricing corrections (used to train your personal AI adapter)\n• Basic profile information you provide",
        "Cloud Sync" to "When you connect to the internet, only your text-based corrections are synced to our backend to train your personalized LoRA adapter. The trained adapter (a tiny 3MB file) is sent back to your device. No images or audio are ever uploaded.",
        "Third-Party Sharing" to "KALA-X does not sell, share, or distribute your personal data to any third parties. Your craft, your data, your control.",
        "Data Deletion" to "You can delete all your data at any time by clearing app storage from your device settings. Cloud-synced corrections can be purged by contacting support."
    )

    LazyColumn(modifier = Modifier.heightIn(max = 500.dp)) {
        items(sections.size) { index ->
            val (title, body) = sections[index]
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = if (index > 0) 20.dp else 0.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

// ─── Help & Support Content ──────────────────────────────────────────

@Composable
fun ColumnScope.HelpContent() {
    val faqs = listOf(
        "How do I create a product listing?" to "Tap 'Create Product' on the Home screen. Take a photo of your handmade item, describe it using your voice in any language, and let the AI generate a professional listing with pricing.",
        "What languages are supported?" to "KALA-X supports voice input in Tamil, Hindi, Telugu, Kannada, Malayalam, and English. The AI automatically translates and polishes your description into market-ready English.",
        "How does AI pricing work?" to "Our Smart Pricing Engine analyzes your material costs, labor time, packaging, and current market trends to suggest a competitive price. You can always adjust the final price.",
        "Do I need internet to use the app?" to "Most features work completely offline! Photo enhancement, voice transcription, and catalog generation all run on your device. Internet is only needed to sync your learning data and publish listings.",
        "How does the Personal Learning Loop work?" to "Every time you correct a price or edit a description, KALA-X remembers your preferences. Over time, the AI generates listings that perfectly match your unique style and pricing strategy."
    )

    LazyColumn(modifier = Modifier.heightIn(max = 500.dp)) {
        items(faqs.size) { index ->
            val (question, answer) = faqs[index]
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = question,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = answer,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Still need help?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reach out to us at\nsupport@kalax.app",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── About KALA-X Content ────────────────────────────────────────────

@Composable
fun ColumnScope.AboutContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        // App Icon / Logo area
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "K",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "KALA-X",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 2.sp
        )
        Text(
            text = "From Handmade to Market-Ready",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Version 1.0.0",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "KALA-X is an AI-powered platform designed to empower traditional artisans and craftsmen. We bridge the gap between handmade craftsmanship and modern e-commerce by providing a frictionless experience to digitize, price, and publish artisan goods — all powered by on-device edge AI that respects your privacy.",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 22.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Tech highlights
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Powered By", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(12.dp))
            AboutFeatureRow("🤖", "Llama 3B Instruct (On-Device)")
            AboutFeatureRow("👁", "MediaPipe Vision (Edge AI)")
            AboutFeatureRow("🧠", "Personal Learning Loop (LoRA)")
            AboutFeatureRow("🔒", "Privacy-First Architecture")
            AboutFeatureRow("🌍", "Multi-Language Support")
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Built with ❤ for artisans everywhere.",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "© 2026 KALA-X Team. All rights reserved.",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AboutFeatureRow(emoji: String, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

// ─── Existing Components (updated) ──────────────────────────────────

@Composable
fun ProfileStatBox(modifier: Modifier, icon: ImageVector, value: String, label: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun SettingItem(icon: ImageVector, title: String, value: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
        }
        if (value != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SettingToggleItem(icon: ImageVector, title: String, checked: Boolean) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
        }
        Switch(
            checked = isChecked, 
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}
