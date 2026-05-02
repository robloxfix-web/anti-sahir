package com.antisahir.app

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anti_sahir_prefs", Context.MODE_PRIVATE)

    fun setStartSleepTime(hour: Int, minute: Int) {
        prefs.edit().putInt("start_hour", hour).putInt("start_minute", minute).apply()
    }

    fun setCurrentSleepTime(hour: Int, minute: Int) {
        prefs.edit().putInt("current_hour", hour).putInt("current_minute", minute).apply()
    }

    fun getCurrentHour(): Int = prefs.getInt("current_hour", 23)
    fun getCurrentMinute(): Int = prefs.getInt("current_minute", 0)
    fun getStartHour(): Int = prefs.getInt("start_hour", 23)
    fun getStartMinute(): Int = prefs.getInt("start_minute", 0)

    fun setStartDate(millis: Long) = prefs.edit().putLong("start_date", millis).apply()
    fun getStartDate(): Long = prefs.getLong("start_date", 0L)

    fun setActive(active: Boolean) = prefs.edit().putBoolean("is_active", active).apply()
    fun isActive(): Boolean = prefs.getBoolean("is_active", false)

    fun reset() = prefs.edit().clear().apply()
}
