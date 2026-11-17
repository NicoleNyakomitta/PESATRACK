
package com.example.pesatrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val LoginNavy = Color(0xFF0B1F49)
private val LoginGold = Color(0xFFE5B85C)
private val LoginBackground = Color(0xFFF7F8FC)

@Composable
fun LoginScreen(
    onSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "PesaTrack",
                color = LoginNavy,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TRACK  •  SAVE  •  GROW",
                color = LoginGold,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(38.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = LoginNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sign in to manage your money and stay on top of your finances.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = "Email Address",
                        color = LoginNavy,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your email")
                        },
                        singleLine = true,
                        isError = emailError.isNotEmpty(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (emailError.isNotEmpty()) {
                        Text(
                            text = emailError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Password",
                        color = LoginNavy,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your password")
                        },
                        singleLine = true,
                        isError = passwordError.isNotEmpty(),
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Text(
                                    text = if (passwordVisible) "Hide" else "Show",
                                    color = LoginNavy
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Button(
                        onClick = {
                            emailError = ""
                            passwordError = ""

                            val emailPattern =
                                android.util.Patterns.EMAIL_ADDRESS

                            if (email.isBlank()) {
                                emailError = "Please enter your email."
                            } else if (!emailPattern.matcher(email.trim()).matches()) {
                                emailError = "Please enter a valid email address."
                            }

                            if (password.isBlank()) {
                                passwordError = "Please enter your password."
                            } else if (password.length < 8) {
                                passwordError =
                                    "Password must contain at least 8 characters."
                            }

                            if (emailError.isEmpty() &&
                                passwordError.isEmpty()
                            ) {
                                onSignIn()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginNavy
                        )
                    ) {
                        Text(
                            text = "Sign In",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Manage your money. Build better habits.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "PesaTrack © 2026",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
