package com.example.teratrack_ver30.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teratrack_ver30.R

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit //Added this new parameter
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon), contentDescription = "Login",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "TeraTrack",
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Login", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Username", color = Color.Black) },

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black
            )


        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", color = Color.Black) },
            visualTransformation = PasswordVisualTransformation(),

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,     // Color when typing
                unfocusedTextColor = Color.Black,   // Color before typing
                focusedBorderColor = Color.Black,   // Border color when clicked
                unfocusedBorderColor = Color.Gray,  // Border color when not clicked
                cursorColor = Color.Black           // The blinking cursor
            )

        )

        Spacer(modifier = Modifier.height(20.dp))

        // Login Button
        Button(onClick = { onLoginClick(username, password) }) {
            Text(text = "Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 👇 NEW: Create Account Button
        OutlinedButton(onClick = { onCreateAccountClick() }) {
            Text(text = "Create Account", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Forgot Password",
            modifier = Modifier.clickable { onForgotPasswordClick() },
            color = Color.Black
        )
    }
}