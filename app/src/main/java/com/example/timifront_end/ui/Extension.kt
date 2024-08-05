package com.example.timifront_end.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SoftwareItem(
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val websiteUrl: String? = null
)


@Composable
fun Extension() {
    val items = listOf(
        SoftwareItem("Odoo", "Logiciel de caisse", "https://external-content.duckduckgo.com/iu/?u=http%3A%2F%2Flogos-download.com%2Fwp-content%2Fuploads%2F2016%2F10%2FOdoo_logo.png&f=1&nofb=1&ipt=9a9cdc8bc343e2e1e9ca1ea930160ee2a9530d52032281e162825738bfadb09d&ipo=images")
    )

    SoftwareList(items)
}


@Composable
fun SoftwareList(softwareItems: List<SoftwareItem>) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(softwareItems) { item ->
            SoftwareItemCard(item)
        }
    }
}

@Composable
fun SoftwareItemCard(item: SoftwareItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { // open website

                },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = item.name, style = MaterialTheme.typography.headlineMedium)
                Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
