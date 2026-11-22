
package com.example.teratrack_ver30

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.teratrack_ver30.repository.UserRepository
import kotlinx.coroutines.launch


@Composable
fun ForgotPasswordScreen(
    userRepository: UserRepository,
    onPasswordReset: () -> Unit,
    onBackToLogin: () -> Unit
) {

    var username by remember { mutableStateOf("") }
    var favouriteWord by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Favourite Word
        OutlinedTextField(
            value = favouriteWord,
            onValueChange = { favouriteWord = it },
            label = { Text("Favourite Word") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // New Password
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Error Message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reset Button
        Button(
            onClick = {

                if (username.isBlank() ||
                    favouriteWord.isBlank() ||
                    newPassword.isBlank()
                ) {
                    errorMessage = "Please complete all fields."
                    return@Button
                }

                if (newPassword.length < 8) {
                    errorMessage = "Password must be at least 8 characters."
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                coroutineScope.launch {

                    try {
                        val account = userRepository.getUser(username.trim())

                        if (account == null) {

                            errorMessage = "Username not found."

                        } else if (
                            !account.favouriteWord.trim().equals(
                                favouriteWord.trim(),
                                ignoreCase = true
                            )
                        ) {

                            errorMessage = "Incorrect favourite word."

                        } else {

                            val updated = userRepository.updateUserPassword(
                                username.trim(),
                                newPassword
                            )

                            if (updated) {
                                onPasswordReset()
                            } else {
                                errorMessage = "Password update failed."
                            }
                        }

                    } catch (e: Exception) {
                        errorMessage = "Something went wrong. Please try again."
                    }

                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Reset Password")
            }
        }

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }
    }
}
