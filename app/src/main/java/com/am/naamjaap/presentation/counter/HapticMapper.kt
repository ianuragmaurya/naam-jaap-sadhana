package com.am.naamjaap.presentation.counter

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.am.naamjaap.data.local.datastore.HapticIntensity

fun performTapFeedback(context: Context, intensity: HapticIntensity) {
    android.util.Log.d("HapticDebug", "performTapFeedback called with intensity=$intensity")

    val vibrator = getVibrator(context)
    android.util.Log.d("HapticDebug", "hasVibrator=${vibrator.hasVibrator()}, hasAmplitudeControl=${vibrator.hasAmplitudeControl()}")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Android 11 (API 30) and above: Use pure predefined effects for modern LRA motors.
        // This is the cleanest, Google-recommended approach for modern devices (Android 13+).
        val effectId = when (intensity) {
            HapticIntensity.OFF -> return
            HapticIntensity.LIGHT -> VibrationEffect.EFFECT_TICK
            HapticIntensity.MEDIUM -> VibrationEffect.EFFECT_CLICK
            HapticIntensity.STRONG -> VibrationEffect.EFFECT_HEAVY_CLICK
        }
        vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        android.util.Log.d("HapticDebug", "vibrate(R-Predefined) called for intensity=$intensity")
        
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10 (API 29): Some devices have weak predefined effects.
        // We use slightly more tactile fallbacks here.
        when (intensity) {
            HapticIntensity.OFF -> return
            HapticIntensity.LIGHT -> {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            }
            HapticIntensity.MEDIUM -> {
                vibrator.vibrate(VibrationEffect.createOneShot(30L, 140))
            }
            HapticIntensity.STRONG -> {
                vibrator.vibrate(VibrationEffect.createOneShot(50L, 255))
            }
        }
        android.util.Log.d("HapticDebug", "vibrate(Q-Enhanced) called for intensity=$intensity")
    } else {
        // API 26-28: Use OneShot with amplitude control check
        val (durationMs, amplitude) = when (intensity) {
            HapticIntensity.OFF -> return
            HapticIntensity.LIGHT -> 30L to 60
            HapticIntensity.MEDIUM -> 50L to 150
            HapticIntensity.STRONG -> 80L to 255
        }
        
        // If device doesn't support amplitude control, use DEFAULT_AMPLITUDE
        val finalAmplitude = if (vibrator.hasAmplitudeControl()) amplitude else VibrationEffect.DEFAULT_AMPLITUDE
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, finalAmplitude))
        android.util.Log.d("HapticDebug", "vibrate(Legacy-OneShot) called")
    }
}

private fun getVibrator(context: Context): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}