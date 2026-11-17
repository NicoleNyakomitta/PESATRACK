
package com.example.pesatrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesatrack.data.model.SavingsGoal
import com.example.pesatrack.data.repository.SavingsGoalRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavingsGoalViewModel(
    private val repository: SavingsGoalRepository
) : ViewModel() {

    val allGoals: Flow<List<SavingsGoal>> =
        repository.allGoals

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

    private fun performOperation(
        successText: String,
        errorText: String,
        operation: suspend () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            clearMessages()

            try {
                operation()
                _successMessage.value = successText
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _errorMessage.value = errorText
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun insertGoal(goal: SavingsGoal) {
        performOperation(
            successText = "Savings goal created successfully.",
            errorText = "Couldn't create your savings goal. Please try again."
        ) {
            repository.insertGoal(goal)
        }
    }

    fun updateGoal(goal: SavingsGoal) {
        performOperation(
            successText = "Savings goal updated successfully.",
            errorText = "Couldn't update your savings goal. Please try again."
        ) {
            repository.updateGoal(goal)
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        performOperation(
            successText = "Savings goal deleted successfully.",
            errorText = "Couldn't delete your savings goal. Please try again."
        ) {
            repository.deleteGoal(goal)
        }
    }
}
