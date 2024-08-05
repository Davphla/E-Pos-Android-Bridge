package com.example.timifront_end.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp


// TODO add documentation, and explanation of the application
// TODO Add a button to copy IP address
@Composable
fun Home() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Home",
            modifier = Modifier.padding(10.dp),
            style = MaterialTheme.typography.headlineMedium,
            textDecoration = TextDecoration.Underline
        )
        IsBackup()
        Logs()
    }
}


@Composable
fun IsBackup() {
    Text(text = "Backup OK")
}

@Composable
fun Logs() {
    val logs by remember {
        mutableStateOf("Server setup : port 8080")
    }
    Text(text = logs)
}

