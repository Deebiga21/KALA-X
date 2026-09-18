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
import androidx.compose.material3.MaterialTheme
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
        val targetStatus = if (selectedFilter == "Drafts") "Draft" else selectedFilter
        (selectedFilter == "All" || it.status == targetStatus) &&
        (it.name.contains(searchQuery, ignoreCase = true))
    }

    val cardBg = MaterialTheme.colorScheme.surface
    val cyanGlow = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("My Catalog", color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Your digital shelf of handmade products.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search and Create Row
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Default.Search, tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = null) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { onNavigate("CreateProduct") },
                    colors = ButtonDefaults.buttonColors(containerColor = cyanGlow),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Product", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
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
                    Text("No products found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCatalog) { product ->
                        CatalogProductCard(
                            product = product,
                            onClick = {
                                if (product.status == "Published") {
                                    // Navigate to details
                                    onNavigate("ProductDetail/${product.id}")
                                }
                            },
                            onDelete = {
                                viewModel.deleteProduct(product.id.toString())
                            }
                        )
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
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatBox(modifier: Modifier, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.1f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp)
    ) {
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
fun CatalogProductCard(
    product: com.example.kalax.ui.product.ProductDraft,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Product") },
            text = { Text("Are you sure you want to delete this product?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.75f).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().weight(1.2f).background(MaterialTheme.colorScheme.surfaceVariant)) {
                if (product.enhancedImageUri != null || product.imageUri != null) {
                    val uriToLoad = product.enhancedImageUri ?: product.imageUri
                    coil.compose.AsyncImage(
                        model = uriToLoad?.let { java.io.File(it) },
                        contentDescription = "Product Image",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
            Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(12.dp)) {
                Text(product.name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text(product.category, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹${product.recommendedPrice}", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
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
                    
                    Text("${product.score}/100", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                }
            }
        }
    }
}
