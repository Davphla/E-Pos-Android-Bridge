package com.example.timifront_end.data.source.local

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class Tickets(val ticket: ByteArray, val time: String)

class LRUCache<K, V>(private val capacity: Int) : LinkedHashMap<K, V>(capacity, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
        return size > capacity
    }
}

class SaveTicket {
    private val tickets = LRUCache<String, Tickets>(1000) // Set your desired capacity

    @RequiresApi(Build.VERSION_CODES.O)
    fun save(ticket: ByteArray) {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val currentTime = currentDateTime.format(formatter)

        tickets[currentTime] = Tickets(ticket, currentTime)
    }

    fun load(): MutableList<Tickets> {
        return tickets.values.toMutableList()
    }
}


