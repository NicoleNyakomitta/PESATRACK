package com.example.pesatrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesatrack.data.model.UserEntity
import com.example.pesatrack.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun registerUser(user: UserEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            clearMessages()
            try {
                val existing = repository.getUserByEmail(user.email)
                if (existing != null) {
                    _errorMessage.value = "Email already registered. Please sign in."
                } else {
                    val id = repository.insertUser(user)
                    if (id != -1L) {
                        _successMessage.value = "Account created successfully! Please sign in."
                        onSuccess()
                    } else {
                        _errorMessage.value = "Registration failed. Please try again."
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginUser(email: String, password: String, onSuccess: (UserEntity) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            clearMessages()
            try {
                val user = repository.login(email, password)
                if (user != null) {
                    onSuccess(user)
                } else {
                    val existingByEmail = repository.getUserByEmail(email)
                    if (existingByEmail != null) {
                        _errorMessage.value = "Incorrect password."
                    } else {
                        _errorMessage.value = "Account not found for $email. Please sign up first."
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
