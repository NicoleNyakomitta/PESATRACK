
package com.example.pesatrack.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesatrack.WorkManagerScheduler
import com.example.pesatrack.data.model.Transaction
import com.example.pesatrack.data.repository.TransactionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    val allTransactions: Flow<List<Transaction>> =
        repository.allTransactions

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

    fun insertTransaction(
        transaction: Transaction,
        context: Context
    ) {
        performOperation(
            successText = "Transaction saved successfully.",
            errorText = "Couldn't save your transaction. Please try again."
        ) {
            repository.insertTransaction(transaction)

            // Check daily spending after a successful expense transaction
            if (transaction.type.equals("Expense", ignoreCase = true)) {

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                val startOfDay = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val startOfTomorrow = calendar.timeInMillis

                val todaysTransactions = repository.allTransactions.first()

                val todaysExpenses = todaysTransactions
                    .filter {
                        it.type.equals("Expense", ignoreCase = true) &&
                                it.date >= startOfDay &&
                                it.date < startOfTomorrow
                    }
                    .sumOf { it.amount }

                WorkManagerScheduler.checkSpendingLimit(
                    context.applicationContext,
                    todaysExpenses
                )
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        performOperation(
            successText = "Transaction updated successfully.",
            errorText = "Couldn't update your transaction. Please try again."
        ) {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        performOperation(
            successText = "Transaction deleted successfully.",
            errorText = "Couldn't delete your transaction. Please try again."
        ) {
            repository.deleteTransaction(transaction)
        }
    }
}
