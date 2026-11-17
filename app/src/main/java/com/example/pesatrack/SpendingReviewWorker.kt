
package com.example.pesatrack

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class SpendingReviewWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {

            val totalSpent = inputData.getDouble("total_spending", 0.0)
            val spendingLimit = inputData.getDouble("spending_limit", 1000.0)

            if (totalSpent < spendingLimit) {
                return Result.success()
            }

            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.success()
            }

            val channelId = "spending_limit_channel"

            val notificationManager =
                applicationContext.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Spending Limit Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                )

                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(
                applicationContext,
                channelId
            )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Daily Spending Limit Exceeded!")
                .setContentText(
                    "You have spent KSh $totalSpent. Your daily limit is KSh $spendingLimit."
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext)
                .notify(1001, notification)

            Result.success()

        } catch (exception: Exception) {
            Result.retry()
        }
    }
}
