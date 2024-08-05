package com.example.timifront_end.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timifront_end.MainActivity
import com.example.timifront_end.data.source.local.TokenData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.NetworkInterface


fun getIPAddress(): String? {
    val interfaces = NetworkInterface.getNetworkInterfaces()
    while (interfaces.hasMoreElements()) {
        val networkInterface = interfaces.nextElement()
        val addresses = networkInterface.inetAddresses
        while (addresses.hasMoreElements()) {
            val address = addresses.nextElement()
            if (!address.isLoopbackAddress && address is Inet4Address) {
                return address.hostAddress
            }
        }
    }
    return "IP Address not found"
}


@Composable
fun Informations(navController: NavController, scope: CoroutineScope) {
    val context = LocalContext.current
    val tokenData = TokenData(context = context)

    fun disconnect() {
        scope.launch {
            tokenData.writeToken("")
            navController.navigate("LoginScreen")
        }
    }
    // Display random information about the application
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Information",
            style = MaterialTheme.typography.headlineMedium,
            textDecoration = TextDecoration.Underline
        )
        Text("Version 1.0.0")
        Text("Developed by Timi")
        Text("Contact: timi.timi@timi.fr")

        Text("IP : " + getIPAddress())

        Button(onClick = { disconnect() }) {
            Text("Disconnect")
        }
    }
}