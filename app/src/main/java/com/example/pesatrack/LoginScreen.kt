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
import androidx.compose.ui.unit.dp
import com.example.pesatrack.data.model.UserEntity
import com.example.pesatrack.viewmodel.UserViewModel

private val LoginNavy = Color(0xFF0B1F49)
private val LoginGold = Color(0xFFE5B85C)
private val LoginBackground = Color(0xFFF7F8FC)

data class UserAccount(
    val fullName: String,
    val email: String,
    val password: String
)

@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onSignIn: (UserAccount) -> Unit // Passes the logged-in user back to MainActivity/NavHost
) {
    // Toggle between Sign In (false) and Sign Up / Registration (true)
    var isSignUpMode by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var localStatusMessage by remember { mutableStateOf("") }

    val dbErrorMessage by viewModel.errorMessage.collectAsState()
    val dbSuccessMessage by viewModel.successMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if (isSignUpMode) "Create Account" else "Welcome Back!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = LoginNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isSignUpMode) {
                            "Register to start tracking your expenses and savings goals."
                        } else {
                            "Sign in to manage your money and stay on top of your finances."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    val displayMessage = dbErrorMessage ?: dbSuccessMessage ?: localStatusMessage
                    if (displayMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (dbErrorMessage != null) Color(0xFFFDECEA) else Color(0xFFE8F0FE)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = displayMessage,
                                    color = if (dbErrorMessage != null) MaterialTheme.colorScheme.error else LoginNavy,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Full Name Field (Only visible during Registration)
                    if (isSignUpMode) {
                        Text(
                            text = "Full Name",
                            color = LoginNavy,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                nameError = ""
                                viewModel.clearMessages()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your full name") },
                            singleLine = true,
                            isError = nameError.isNotEmpty(),
                            shape = RoundedCornerShape(14.dp)
                        )
                        if (nameError.isNotEmpty()) {
                            Text(
                                text = nameError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Email Field
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
                            localStatusMessage = ""
                            viewModel.clearMessages()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your email") },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
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
                            localStatusMessage = ""
                            viewModel.clearMessages()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your password") },
                        singleLine = true,
                        isError = passwordError.isNotEmpty(),
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
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

                    // Confirm Password Field (Only visible during Registration)
                    if (isSignUpMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Confirm Password",
                            color = LoginNavy,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordError = ""
                                localStatusMessage = ""
                                viewModel.clearMessages()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Confirm your password") },
                            singleLine = true,
                            isError = confirmPasswordError.isNotEmpty(),
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            shape = RoundedCornerShape(14.dp)
                        )
                        if (confirmPasswordError.isNotEmpty()) {
                            Text(
                                text = confirmPasswordError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (isSignUpMode) {
                                var valid = true
                                if (fullName.isBlank()) {
                                    nameError = "Full name cannot be empty"
                                    valid = false
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    emailError = "Enter a valid email address"
                                    valid = false
                                }
                                if (password.length < 6) {
                                    passwordError = "Password must be at least 6 characters"
                                    valid = false
                                }
                                if (password != confirmPassword) {
                                    confirmPasswordError = "Passwords do not match"
                                    valid = false
                                }
                                if (valid) {
                                    val newUser = UserEntity(
                                        fullName = fullName,
                                        email = email.trim(),
                                        password = password
                                    )
                                    viewModel.registerUser(
                                        user = newUser,
                                        onSuccess = {
                                            localStatusMessage = "Account registered successfully in DB! Please sign in."
                                            isSignUpMode = false
                                            password = ""
                                            confirmPassword = ""
                                        }
                                    )
                                }
                            } else {
                                var valid = true
                                if (email.isBlank()) {
                                    emailError = "Email cannot be empty"
                                    valid = false
                                }
                                if (password.isBlank()) {
                                    passwordError = "Password cannot be empty"
                                    valid = false
                                }
                                if (valid) {
                                    viewModel.loginUser(
                                        email = email.trim(),
                                        password = password,
                                        onSuccess = { userEntity ->
                                            onSignIn(
                                                UserAccount(
                                                    fullName = userEntity.fullName,
                                                    email = userEntity.email,
                                                    password = userEntity.password
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LoginNavy),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isSignUpMode) "Register" else "Sign In",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account?" else "Don't have an account?",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(
                            onClick = {
                                isSignUpMode = !isSignUpMode
                                localStatusMessage = ""
                                nameError = ""
                                emailError = ""
                                passwordError = ""
                                confirmPasswordError = ""
                                viewModel.clearMessages()
                            }
                        ) {
                            Text(
                                text = if (isSignUpMode) "Sign In" else "Sign Up",
                                color = LoginNavy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
