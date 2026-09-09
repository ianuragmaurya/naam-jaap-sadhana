package com.am.naamjaap.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val WORK_NAME = "naam_jaap_daily_reminder_work"

    fun schedule(context: Context, hour: Int, minute: Int) {
        val delay = calculateInitialDelay(hour, minute)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.toMinutes(), TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    /**
     * Called by the worker itself after it fires, to reschedule for the
     * same time exactly 24 hours later — achieves a reliable daily repeat
     * at an exact HH:mm, which PeriodicWorkRequest cannot guarantee.
     */
    fun scheduleNext(context: Context) {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Duration {
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(LocalTime.of(hour, minute))
        if (target.isBefore(now)) {
            target = target.plusDays(1)
        }
        return Duration.between(now, target)
    }
}