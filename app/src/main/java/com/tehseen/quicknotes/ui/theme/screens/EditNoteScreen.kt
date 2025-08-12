package com.tehseen.quicknotes.ui.theme.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tehseen.quicknotes.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(viewModel: NoteViewModel, noteId: String, onBack: () -> Unit) {
    val isNew = noteId == "new"
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val notes by viewModel.notes.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId, notes) {
        if (!isNew) {
            val idLong = noteId.toLongOrNull()
            idLong?.let {
                notes.find { it.id == idLong }?.let { note ->
                    title = note.title
                    content = note.content
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "New Note" else "Edit Note", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.upsert(title, content, if (isNew) null else noteId.toLong())
                        onBack()
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = "Save Note")
                    }
                    if (!isNew) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Note", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                minLines = 5
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    noteId.toLongOrNull()?.let { id ->
                        notes.find { it.id == id }?.let { note ->
                            viewModel.delete(note)
                        }
                    }
                    showDeleteDialog = false
                    onBack()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") }
        )
    }
}
