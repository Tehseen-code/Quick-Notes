package com.tehseen.quicknotes.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun allNotes(): Flow<List<Note>> = dao.getAllNotesFlow()
    suspend fun getNote(id: Long) = dao.getNoteById(id)
    suspend fun upsert(note: Note) = dao.upsert(note)
    suspend fun delete(note: Note) = dao.delete(note)
    suspend fun deleteAll() = dao.deleteAll()
}
