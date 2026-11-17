
package com.example.pesatrack

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object WorkManagerScheduler {

    fun checkSpendingLimit(
        context: Context,
        totalSpent: Double
    ) {
        val inputData = Data.Builder()
            .putDouble("total_spending", totalSpent)
            .putDouble("spending_limit", 1000.0)
            .build()

        val request =
            OneTimeWorkRequestBuilder<SpendingReviewWorker>()
                .setInputData(inputData)
                .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
