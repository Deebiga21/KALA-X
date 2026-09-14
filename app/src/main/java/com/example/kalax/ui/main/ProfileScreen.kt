package com.example.kalax.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF020617))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("My Profile", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    // Profile Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.Gray))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Artisan Name", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Handicraft Artisan", color = Color(0xFF06B6D4), fontSize = 14.sp)
                            Text("Tamil Nadu, India", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Stats
                    Text("MY BUSINESS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(0.6f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard("${catalog.size}", "Products")
                            StatCard("$published", "Published")
                            StatCard("₹24.6K", "Total Sales")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Settings
                    Text("SETTINGS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingItem("Language", "Tamil")
                    SettingItem("Notifications", "ON")
                    SettingItem("Privacy", ">")
                    SettingItem("Help & Support", ">")
                    
                    Spacer(modifier = Modifier.height(80.dp)) // Nav padding
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
fun SettingItem(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.White, fontSize = 16.sp)
        Text(value, color = Color(0xFF06B6D4), fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
