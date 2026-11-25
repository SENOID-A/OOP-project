package com.example.teratrack_ver30.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teratrack_ver30.repository.UserRepository
import com.example.teratrack_ver30.user.CreateAccount
import kotlinx.coroutines.launch

@Composable
fun CreateAccScreen(
    userRepository: UserRepository,
    onAccountCreated: () -> Unit,
    onBackToLogin: () -> Unit
) {
    // 1. State for the text fields
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var favouriteWord by remember { mutableStateOf("") }

    // 2. State for error messages and loading
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create Account", fontSize = 30.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(24.dp))

        // Username Field
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


        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Security Question Field
        OutlinedTextField(
            value = favouriteWord,
            onValueChange = { favouriteWord = it },
            label = { Text("What is your favourite word? (Security Question)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        // Error Message Display
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Button
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank() || favouriteWord.isBlank()) {
                    errorMessage = "Please fill in all fields."
                    return@Button
                }

                isLoading = true
                errorMessage = null

                coroutineScope.launch {
                    // 3. Check if user already exists in the database
                    val cleanUsername = username.trim()

                    val existingUser = userRepository.getUser(cleanUsername)

                    if (existingUser != null) {
                        errorMessage = "Username is already taken."
                        isLoading = false
                        return@launch
                    }

                    // 4. Use your OOP class to create and validate the account
                    val newAccount = CreateAccount(username, favouriteWord)
                    val validationResult = newAccount.enterPassword(password)

                    if (validationResult == "Password set successfully") {
                        // 5. Save to Room Database via Repository
                        userRepository.createUser(newAccount)
                        isLoading = false
                        onAccountCreated() // Navigate back to login
                    } else {
                        // Password validation failed (e.g., less than 8 characters)
                        errorMessage = validationResult
                        errorMessage = validationResult
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading // Disable button while saving
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Already have an account? Login here.")
        }
    }
}