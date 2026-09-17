package com.example.kalax.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: ProductViewModel
) {
    val catalog by viewModel.catalog.collectAsState()
    val published = catalog.count { it.status == "Published" }
    val drafts = catalog.count { it.status == "Draft" }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredCatalog = catalog.filter {
        (selectedFilter == "All" || it.status == selectedFilter) &&
        (it.name.contains(searchQuery, ignoreCase = true))
    }

    val cardBg = Color(0xFFD5E0B5)
    val cyanGlow = Color(0xFF98B891)

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF1F5E1))) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("My Catalog", color = Color(0xFF4A5D44), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Your digital shelf of handmade products.", color = Color(0xFF697A63), fontSize = 12.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search and Create Row
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...", color = Color(0xFF697A63)) },
                    leadingIcon = { Icon(Icons.Default.Search, tint = Color(0xFF697A63), contentDescription = null) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color(0xFF4A5D44),
                        unfocusedTextColor = Color(0xFF4A5D44)
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { onNavigate("CreateProduct") },
                    colors = ButtonDefaults.buttonColors(containerColor = cyanGlow),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF4A5D44))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Product", color = Color(0xFF4A5D44), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("All", selectedFilter == "All") { selectedFilter = "All" }
                FilterChip("Published", selectedFilter == "Published") { selectedFilter = "Published" }
                FilterChip("Drafts", selectedFilter == "Drafts") { selectedFilter = "Drafts" }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(modifier = Modifier.weight(1f), "${catalog.size}", "Products")
                StatBox(modifier = Modifier.weight(1f), "$published", "Published")
                StatBox(modifier = Modifier.weight(1f), "$drafts", "Drafts")
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (filteredCatalog.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No products found.", color = Color(0xFF697A63))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCatalog) { product ->
                        ProductCard(product)
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) } // padding for bottom nav
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
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) Color(0xFF98B891) else Color(0xFFD5E0B5), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, color = Color(0xFF4A5D44), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatBox(modifier: Modifier, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(1.dp, Color(0xFF4A5D44).copy(0.1f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp)
    ) {
        Text(value, color = Color(0xFF4A5D44), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF697A63), fontSize = 12.sp)
    }
}

@Composable
fun ProductCard(product: com.example.kalax.ui.product.ProductDraft) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.75f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD5E0B5)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF4A5D44).copy(0.05f))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().weight(1.2f).background(Color(0xFFC4D1A4))) {
                if (product.enhancedImageUri != null || product.imageUri != null) {
                    val uriToLoad = product.enhancedImageUri ?: product.imageUri
                    coil.compose.AsyncImage(
                        model = uriToLoad?.let { java.io.File(it) },
                        contentDescription = "Product Image",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color(0xFF4A5D44), modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
            }
            Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(12.dp)) {
                Text(product.name, color = Color(0xFF4A5D44), fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text(product.category, color = Color(0xFF697A63), fontSize = 10.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹${product.recommendedPrice}", color = Color(0xFF4A5D44), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    val isPublished = product.status == "Published"
                    val statusColor = if (isPublished) Color(0xFF10B981) else Color(0xFFF59E0B)
                    Row(
                        modifier = Modifier.background(statusColor.copy(0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(statusColor, RoundedCornerShape(3.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(product.status, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Text("${product.score}/100", color = Color(0xFF697A63), fontSize = 10.sp)
                }
            }
        }
    }
}
