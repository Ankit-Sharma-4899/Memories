@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.memories.memories.presentation.dashboard

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.memories.memories.domain.model.Memory
import com.memories.memories.domain.model.UserProfile
import com.memories.memories.presentation.components.MemoryScaffold
import java.util.Calendar

@Composable
fun DashboardScreen(
    userProfile: UserProfile?,
    uiState: MemoryUiState,
    snackbarHostState: SnackbarHostState,
    onSearchChanged: (String) -> Unit,
    onAddClicked: () -> Unit,
    onDismissAdd: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onPhotosSelected: (List<Uri>) -> Unit,
    onSaveMemory: () -> Unit,
    onDeleteMemory: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val filteredMemories = remember(uiState.memories, uiState.searchQuery) {
        if (uiState.searchQuery.isBlank()) {
            uiState.memories
        } else {
            uiState.memories.filter { memory ->
                memory.title.contains(uiState.searchQuery, ignoreCase = true) ||
                    memory.date.contains(uiState.searchQuery, ignoreCase = true) ||
                    memory.note.contains(uiState.searchQuery, ignoreCase = true)
            }
        }
    }

    MemoryScaffold(
        snackbarHostState = snackbarHostState,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Add memory")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 92.dp)
        ) {
            item {
                DashboardHero(
                    userProfile = userProfile,
                    memoryCount = uiState.memories.size,
                    photoCount = uiState.memories.sumOf { it.photoUrls.size },
                    onSignOut = onSignOut
                )
            }
            item {
                SearchBar(value = uiState.searchQuery, onValueChanged = onSearchChanged)
            }
            if (uiState.showAddSheet) {
                item {
                    AddMemoryPanel(
                        uiState = uiState,
                        onDismiss = onDismissAdd,
                        onTitleChanged = onTitleChanged,
                        onDateChanged = onDateChanged,
                        onNoteChanged = onNoteChanged,
                        onPhotosSelected = onPhotosSelected,
                        onSaveMemory = onSaveMemory
                    )
                }
            }
            item {
                Text(
                    text = "Memory folders",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }
            if (uiState.isLoading && uiState.memories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (filteredMemories.isEmpty()) {
                item {
                    EmptyState(onAddClicked)
                }
            } else {
                items(filteredMemories, key = { it.id }) { memory ->
                    MemoryCard(memory = memory, onDelete = { onDeleteMemory(memory.id) })
                }
            }
        }
    }
}

@Composable
private fun DashboardHero(
    userProfile: UserProfile?,
    memoryCount: Int,
    photoCount: Int,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile?.name?.firstOrNull()?.uppercase() ?: "M",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "Hello, ${userProfile?.name ?: "Memory keeper"}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "@${userProfile?.username ?: "memories"}",
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.65f)
                )
            }
            IconButton(onClick = onSignOut) {
                Icon(Icons.Default.Logout, contentDescription = "Sign out", tint = MaterialTheme.colorScheme.onSecondary)
            }
        }
        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("Memories", memoryCount.toString(), Modifier.weight(1f))
            StatCard("Photos", photoCount.toString(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.headlineSmall)
            Text(label, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.68f))
        }
    }
}

@Composable
private fun SearchBar(value: String, onValueChanged: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        label = { Text("Search by date, title or note") }
    )
}

@Composable
private fun AddMemoryPanel(
    uiState: MemoryUiState,
    onDismiss: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onPhotosSelected: (List<Uri>) -> Unit,
    onSaveMemory: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val picker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateChanged("%02d/%02d/%04d".format(dayOfMonth, month + 1, year))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents(),
        onResult = onPhotosSelected
    )

    Card(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Add memory",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = uiState.date,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                label = { Text("Date") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                trailingIcon = {
                    TextButton(onClick = { picker.show() }) {
                        Text("Select")
                    }
                },
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Note") },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { photoPicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Text(
                    text = if (uiState.selectedPhotoUris.isEmpty()) "Choose photos" else "${uiState.selectedPhotoUris.size} photos selected",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (uiState.selectedPhotoUris.isNotEmpty()) {
                SelectedPreview(uri = uiState.selectedPhotoUris.first())
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSaveMemory,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Text("Save to Firebase", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun SelectedPreview(uri: Uri) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(14.dp)),
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageURI(uri)
            }
        },
        update = { imageView -> imageView.setImageURI(uri) }
    )
}

@Composable
private fun EmptyState(onAddClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.42f), RoundedCornerShape(18.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
        Text(
            text = "No memories yet",
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Add photos, choose a date, and Firebase will store the folder for your account.",
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
        )
        TextButton(onClick = onAddClicked) {
            Text("Add first memory")
        }
    }
}

@Composable
private fun MemoryCard(memory: Memory, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 7.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.8f)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.26f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.48f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.34f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(memory.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${memory.date} · ${memory.photoUrls.size} photos",
                        modifier = Modifier.padding(top = 2.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                    )
                    if (memory.note.isNotBlank()) {
                        Text(
                            text = memory.note,
                            modifier = Modifier.padding(top = 6.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete memory", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
