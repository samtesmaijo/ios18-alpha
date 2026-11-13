package com.complux.ios18sim.data

import android.content.Context

/** Small, local-only preference store for the simulated system. */
class SimulatorPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("ios18_simulator", Context.MODE_PRIVATE)

    fun read(): Snapshot = Snapshot(
        darkMode = prefs.getBoolean(KEY_DARK, false),
        wallpaper = prefs.getInt(KEY_WALLPAPER, 0).coerceIn(0, 4),
        brightness = prefs.getFloat(KEY_BRIGHTNESS, 0.72f).coerceIn(0f, 1f),
        volume = prefs.getFloat(KEY_VOLUME, 0.58f).coerceIn(0f, 1f),
        wifi = prefs.getBoolean(KEY_WIFI, true),
        bluetooth = prefs.getBoolean(KEY_BLUETOOTH, false),
        airplane = prefs.getBoolean(KEY_AIRPLANE, false),
        focus = prefs.getBoolean(KEY_FOCUS, false),
        performance = prefs.getString(KEY_PERFORMANCE, "Balanced") ?: "Balanced",
        glassLab = prefs.getBoolean(KEY_GLASS, false),
        note = prefs.getString(KEY_NOTE, "") ?: "",
        photoUris = prefs.getString(KEY_PHOTO_URIS, "") ?: ""
    )

    fun write(snapshot: Snapshot) {
        prefs.edit()
            .putBoolean(KEY_DARK, snapshot.darkMode)
            .putInt(KEY_WALLPAPER, snapshot.wallpaper)
            .putFloat(KEY_BRIGHTNESS, snapshot.brightness)
            .putFloat(KEY_VOLUME, snapshot.volume)
            .putBoolean(KEY_WIFI, snapshot.wifi)
            .putBoolean(KEY_BLUETOOTH, snapshot.bluetooth)
            .putBoolean(KEY_AIRPLANE, snapshot.airplane)
            .putBoolean(KEY_FOCUS, snapshot.focus)
            .putString(KEY_PERFORMANCE, snapshot.performance)
            .putBoolean(KEY_GLASS, snapshot.glassLab)
            .putString(KEY_NOTE, snapshot.note)
            .putString(KEY_PHOTO_URIS, snapshot.photoUris)
            .apply()
    }

    data class Snapshot(
        val darkMode: Boolean = false,
        val wallpaper: Int = 0,
        val brightness: Float = 0.72f,
        val volume: Float = 0.58f,
        val wifi: Boolean = true,
        val bluetooth: Boolean = false,
        val airplane: Boolean = false,
        val focus: Boolean = false,
        val performance: String = "Balanced",
        val glassLab: Boolean = false,
        val note: String = "",
        val photoUris: String = ""
    )

    companion object {
        private const val KEY_DARK = "dark_mode"
        private const val KEY_WALLPAPER = "wallpaper"
        private const val KEY_BRIGHTNESS = "brightness"
        private const val KEY_VOLUME = "volume"
        private const val KEY_WIFI = "wifi"
        private const val KEY_BLUETOOTH = "bluetooth"
        private const val KEY_AIRPLANE = "airplane"
        private const val KEY_FOCUS = "focus"
        private const val KEY_PERFORMANCE = "performance"
        private const val KEY_GLASS = "glass_lab"
        private const val KEY_NOTE = "note"
        private const val KEY_PHOTO_URIS = "photo_uris"
    }
}
