package com.tehseen.quicknotes.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tehseen.quicknotes.data.Note

object JsonUtil {
    private val gson = Gson()
    fun notesToJson(notes: List<Note>): String = gson.toJson(notes)
    fun jsonToNotes(json: String): List<Note> {
        val type = object : TypeToken<List<Note>>() {}.type
        return gson.fromJson(json, type)
    }
}
