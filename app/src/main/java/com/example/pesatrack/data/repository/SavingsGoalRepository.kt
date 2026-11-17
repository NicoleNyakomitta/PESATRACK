
package com.example.pesatrack.data.repository

import com.example.pesatrack.data.local.SavingsGoalDao
import com.example.pesatrack.data.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

class SavingsGoalRepository(
    private val savingsGoalDao: SavingsGoalDao
) {

    val allGoals: Flow<List<SavingsGoal>> =
        savingsGoalDao.getAllGoals()

    suspend fun insertGoal(goal: SavingsGoal) {
        savingsGoalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: SavingsGoal) {
        savingsGoalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: SavingsGoal) {
        savingsGoalDao.deleteGoal(goal)
    }
}
