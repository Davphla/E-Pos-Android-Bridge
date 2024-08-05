package com.example.timifront_end.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timifront_end.MainActivity
import com.example.timifront_end.R
import com.example.timifront_end.data.source.local.TokenData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject


@Composable
fun LoginScreen(navController: NavController, scope: CoroutineScope) {
    val context = LocalContext.current
    val tokenData = TokenData(context = context)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    fun tryLogin(navController: NavController, login: String, password: String) {
        error = ""
        if (login == "" || password == "") {
            error = "Missing login/password"
            return
        }
        scope.launch {
            try {
                val response = sendRequestLogin(login, password)
                Log.d("HTTP", response.toString())
                val token = response.body()?.string()
                if (!response.isSuccessful || token == null) {
                    error = "Incorrect login/password"
                    return@launch
                }
                tokenData.writeToken(token)
                Log.d("token", token)
                navController.navigate("Home")
            } catch (
                e: Exception
            ) {
                error = "Failed to login"
                Log.d("HTTP", "Failed to login : " + e.message)
            }
        }
    }

    fun checkToken() {
        scope.launch {
            val token = tokenData.readToken().first()
            if (token != "") {
                navController.navigate("Home")
            }
        }
    }

    checkToken()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top

    ) {
        Image(
            painter = painterResource(id = R.drawable.login_picture),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.padding(16.dp),
                value = email,
                onValueChange = { email = it },
                label = { Text("Login") },
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = { tryLogin(navController, email, password) }) {
                Text("Submit")
            }
        }
    }
}

// TODO Move this in a separate file
suspend fun sendRequestLogin(login: String, password: String): Response {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("email", login)
            put("password", password)
        }

        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )

        val request = okhttp3.Request.Builder()
            .url("https://api.mytimi.fr/auth/login")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()
        Log.d("HTTP", request.toString())
        client.newCall(request).execute()
    }
}
