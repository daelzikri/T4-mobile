package com.example.studentdirectory.utils

import android.content.Context
import java.io.File

object FileHelper {

    // Nama file: note_[nim].txt, misal note_2024001.txt
    private fun getFileName(nim: String) = "note_${nim}.txt"

    fun saveNote(context: Context, nim: String, content: String): Boolean {
        return try {
            val file = File(context.filesDir, getFileName(nim))
            file.writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loadNote(context: Context, nim: String): String {
        return try {
            val file = File(context.filesDir, getFileName(nim))
            if (file.exists()) file.readText() else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun deleteNote(context: Context, nim: String): Boolean {
        return try {
            val file = File(context.filesDir, getFileName(nim))
            if (file.exists()) file.delete() else true
        } catch (e: Exception) {
            false
        }
    }

    fun isNoteExists(context: Context, nim: String): Boolean {
        return File(context.filesDir, getFileName(nim)).exists()
    }

    fun getNoteSize(context: Context, nim: String): Long {
        val file = File(context.filesDir, getFileName(nim))
        return if (file.exists()) file.length() else 0L
    }
}
