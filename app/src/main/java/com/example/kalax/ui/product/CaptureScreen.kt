package com.example.kalax.ui.product

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun CaptureScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var isUploading by remember { mutableStateOf(false) }

    fun processBitmap(bitmap: Bitmap?) {
        if (bitmap == null) return
        isUploading = true
        val file = File(context.cacheDir, "capture_${UUID.randomUUID()}.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
        viewModel.uploadImage(file)
        viewModel.enhanceImage() // Start edge AI enhancement immediately
        isUploading = false
        onNext()
    }

    fun processUri(uri: Uri?) {
        if (uri == null) return
        isUploading = true
        val file = File(context.cacheDir, "gallery_${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        viewModel.uploadImage(file)
        viewModel.enhanceImage()
        isUploading = false
        onNext()
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        processBitmap(bitmap)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        processUri(uri)
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
                Text("Create Product", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Start with a photo", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Capture Product", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isUploading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Uploading...", color = MaterialTheme.colorScheme.onBackground)
            } else {
                Button(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Camera")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload from Gallery", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
