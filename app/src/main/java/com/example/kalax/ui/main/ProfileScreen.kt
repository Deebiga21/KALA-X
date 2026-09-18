package com.example.kalax.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
                                    Icon(androidx.compose.material.icons.Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
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
                            progress = 0.82f,
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
                    
                    SettingItem(Icons.Default.Language, "Language", preferredLanguage)
                    SettingToggleItem(Icons.Default.Notifications, "Notifications", true)
                    SettingItem(Icons.Default.Lock, "Privacy", null)
                    SettingItem(Icons.Default.Info, "Help & Support", null)
                    SettingItem(Icons.Default.CheckCircle, "About KALA-X", null)
                    
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
fun SettingItem(icon: ImageVector, title: String, value: String?) {
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
        if (value != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
