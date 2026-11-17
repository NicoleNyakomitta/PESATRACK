
package com.example.pesatrack.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pesatrack.data.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {

    @Insert
    suspend fun insertGoal(goal: SavingsGoal)

    @Query("SELECT * FROM savings_goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<SavingsGoal>>

    @Update
    suspend fun updateGoal(goal: SavingsGoal)

    @Delete
    suspend fun deleteGoal(goal: SavingsGoal)
}
