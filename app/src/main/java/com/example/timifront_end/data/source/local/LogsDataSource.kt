package com.example.timifront_end.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import java.util.prefs.Preferences

// TODO Refactor with datastore
class LogsDataSource(private val context: Context) {
    val logMutable: MutableList<String> = mutableListOf()


    fun getLog(): String {
        val prefs = context.getSharedPreferences("log", Context.MODE_PRIVATE)
        return prefs.getString("log", "")!!
    }

    fun addLog(newlog: String) {
        val prefs = context.getSharedPreferences("log", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val log = prefs.getString("log", "")
        editor.putString("log", log + newlog + "\n")
        editor.apply()
        logMutable.add(newlog)
    }

    fun resetLog() {
        val prefs = context.getSharedPreferences("log", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("log", "")
        editor.apply()
        logMutable.clear()
    }

}