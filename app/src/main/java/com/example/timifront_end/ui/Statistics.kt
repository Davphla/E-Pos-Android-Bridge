package com.example.timifront_end.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
//import com.example.timifront_end.lib.SERVER_PORT

var NumberTicketSent = 0;

@Composable
fun Statistic() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        StatisticsSection(NumberTicketSent)
    }
}


@Composable
fun StatisticsSection(ticket: Int) {
    //Title
    Column(Modifier.padding(10.dp)) {
        Text(
            text = "Statistique",
            style = MaterialTheme.typography.headlineMedium,
            textDecoration = TextDecoration.Underline
        )
        Text(text = "Ticket envoyé : $ticket")
    }
}

