package com.example.system

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log

class AndroidSystemManager(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var torchCameraId: String? = null

    init {
        try {
            cameraManager?.cameraIdList?.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK
            }?.let {
                torchCameraId = it
            }
        } catch (e: Exception) {
            Log.e("SystemManager", "Error querying camera for torch", e)
        }
    }

    fun setTorch(enabled: Boolean): Boolean {
        return try {
            val id = torchCameraId ?: cameraManager?.cameraIdList?.firstOrNull()
            if (id != null) {
                cameraManager?.setTorchMode(id, enabled)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("SystemManager", "Failed to toggle torch", e)
            false
        }
    }

    fun getVolumeFraction(): Float {
        return try {
            val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 8
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
            (current.toFloat() / max.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
        } catch (e: Exception) {
            0.5f
        }
    }

    fun setVolumeFraction(fraction: Float) {
        try {
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
            val target = (fraction.coerceIn(0f, 1f) * max).toInt()
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        } catch (e: Exception) {
            Log.e("SystemManager", "Failed to set volume", e)
        }
    }

    fun setWindowBrightness(activity: Activity?, brightnessFraction: Float) {
        try {
            activity?.window?.let { window ->
                val layoutParams = window.attributes
                layoutParams.screenBrightness = brightnessFraction.coerceIn(0.02f, 1.0f)
                window.attributes = layoutParams
            }
        } catch (e: Exception) {
            Log.e("SystemManager", "Failed to set brightness", e)
        }
    }

    fun triggerHapticFeedback(durationMs: Long = 25) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
        } catch (e: Exception) {
            Log.e("SystemManager", "Failed haptic vibration", e)
        }
    }

    fun openWifiSettings() {
        startIntentSafely(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    fun openBluetoothSettings() {
        startIntentSafely(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }

    fun openAirplaneSettings() {
        startIntentSafely(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
    }

    fun openDataSettings() {
        val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
        if (!startIntentSafely(intent)) {
            startIntentSafely(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }

    fun openDndSettings() {
        startIntentSafely(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    fun openDisplaySettings() {
        startIntentSafely(Intent(Settings.ACTION_DISPLAY_SETTINGS))
    }

    fun openDefaultHomeSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            if (!startIntentSafely(intent)) {
                startIntentSafely(Intent(Settings.ACTION_HOME_SETTINGS))
            }
        } else {
            startIntentSafely(Intent(Settings.ACTION_HOME_SETTINGS))
        }
    }

    fun launchCamera() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startIntentSafely(intent)
    }

    fun launchDialer() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startIntentSafely(intent)
    }

    fun launchMessages() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_MESSAGING)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (!startIntentSafely(intent)) {
            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("sms:")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startIntentSafely(fallback)
        }
    }

    fun launchBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.apple.com")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startIntentSafely(intent)
    }

    fun launchPackage(packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (intent != null) {
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("SystemManager", "Failed to launch $packageName", e)
            false
        }
    }

    fun getInstalledApps(): List<Pair<String, String>> {
        return try {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val pm = context.packageManager
            val list = pm.queryIntentActivities(intent, 0)
            list.mapNotNull { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                // Skip our own package from redundant listing
                if (pkg != context.packageName) {
                    val label = resolveInfo.loadLabel(pm).toString()
                    label to pkg
                } else null
            }.sortedBy { it.first }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun startIntentSafely(intent: Intent): Boolean {
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("SystemManager", "Failed to start intent: ${intent.action}", e)
            false
        }
    }
}
