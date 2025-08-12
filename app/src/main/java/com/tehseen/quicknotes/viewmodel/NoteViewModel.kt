package com.tehseen.quicknotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tehseen.quicknotes.data.AppDatabase
import com.tehseen.quicknotes.data.Note
import com.tehseen.quicknotes.data.NoteRepository
import com.tehseen.quicknotes.util.JsonUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NoteViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: NoteRepository
    // Expose a StateFlow to the UI, which is a reactive data holder
    private val _notes = MutableStateFlow(emptyList<Note>())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        val db = AppDatabase.getInstance(app)
        repo = NoteRepository(db.noteDao())
        // Collect notes from the repository and update the StateFlow
        viewModelScope.launch {
            repo.allNotes().collectLatest { notesList ->
                _notes.value = notesList
            }
        }
    }

    // Now uses the StateFlow's current value for a snapshot
    fun getAllNotesSnapshot(): List<Note> {
        return notes.value
    }

    fun upsert(title: String, content: String, id: Long? = null) {
        viewModelScope.launch {
            val note = Note(
                id = id ?: System.currentTimeMillis(),
                title = title,
                content = content,
                updatedAt = System.currentTimeMillis()
            )
            repo.upsert(note)
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch { repo.delete(note) }
    }

    fun importFromJson(json: String) {
        viewModelScope.launch {
            val imported = JsonUtil.jsonToNotes(json)
            imported.forEach { repo.upsert(it) }
        }
    }

    fun exportToJson(): String {
        val current = getAllNotesSnapshot()
        return JsonUtil.notesToJson(current)
    }
}