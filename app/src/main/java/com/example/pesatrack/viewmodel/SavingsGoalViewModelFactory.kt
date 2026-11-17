
package com.example.pesatrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pesatrack.data.repository.SavingsGoalRepository

class SavingsGoalViewModelFactory(
    private val repository: SavingsGoalRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(SavingsGoalViewModel::class.java)) {
            return SavingsGoalViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
