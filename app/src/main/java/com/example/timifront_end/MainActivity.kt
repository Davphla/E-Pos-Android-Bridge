package com.example.timifront_end

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timifront_end.data.source.local.TokenData
import com.example.timifront_end.service.HttpServer.ServerService
import com.example.timifront_end.ui.Extension
import com.example.timifront_end.ui.Home
import com.example.timifront_end.ui.Informations
import com.example.timifront_end.ui.LoginScreen
import com.example.timifront_end.ui.Statistic
import com.example.timifront_end.ui.theme.TimiFrontendTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private fun startService() {
        val intent = Intent(this, ServerService::class.java)
        startForegroundService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_NETWORK_STATE,
            ),
            1
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService()
        askForPermissions()

        lifecycleScope.launch {

            setContent {
                TimiFrontendTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigator()
                    }
                }
            }
        }
    }

    companion object {

        // TODO Change token to datastore
        val mutable_email = mutableStateOf<String>("")
    }

    sealed class Screen(val route: String) {
        data object LoginScreen : Screen("LoginScreen")
        data object Home : Screen("Home")
        data object Statistic : Screen("Statistique")
        data object Extension : Screen("Extension")
        data object Information : Screen("Information")
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()

        Column() {
            NavHost(navController, startDestination = Screen.LoginScreen.route) {
                composable(Screen.LoginScreen.route) { LoginScreen(navController, lifecycleScope) }
                composable(Screen.Home.route) { Home() }
                composable(Screen.Statistic.route) { Statistic() }
                composable(Screen.Extension.route) { Extension() }
                composable(Screen.Information.route) { Informations(navController, lifecycleScope) }
            }
            Spacer(Modifier.weight(1f))
            NavigationBar(navController)
        }

    }

    @Composable
    fun NavigationBar(navController: NavController) {
        data class NavigationItem(val screen: Screen, val icon: ImageVector)

        var currentRoute by remember { mutableStateOf(Screen.Home.route) }

        val items = listOf(
            NavigationItem(Screen.Home, Icons.Default.Home),
            NavigationItem(Screen.Statistic, Icons.Default.Build),
            NavigationItem(Screen.Extension, Icons.Default.Edit),
            NavigationItem(Screen.Information, Icons.Default.Info)
        )

        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    label = { Text(item.screen.route) },
                    icon = { Icon(item.icon, contentDescription = item.screen.route) },
                    onClick = {
                        navController.navigate(item.screen.route);
                        currentRoute = item.screen.route
                    },
                    selected = currentRoute == item.screen.route,
                    alwaysShowLabel = false,
                )
            }
        }
    }

}
