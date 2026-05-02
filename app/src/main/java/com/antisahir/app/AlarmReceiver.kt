package com.antisahir.app

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PowerManager

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PrefsManager(context)

        if (!prefs.isActive()) return

        val curH = prefs.getCurrentHour()
        val curM = prefs.getCurrentMinute()

        // ---- إغلاق الشاشة فوراً ----
        lockScreen(context)

        // ---- حساب الوقت الجديد (تقليص 15 دقيقة) ----
        val targetMinTotal = 22 * 60 + 30
        val currentMinTotal = normalizeMinutes(curH, curM)
        val newMinTotal = (currentMinTotal - 15).coerceAtLeast(targetMinTotal)

        val newH: Int
        val newM: Int
        if (newMinTotal >= 24 * 60) {
            // ما زال بعد منتصف الليل
            val adjusted = newMinTotal - 24 * 60
            newH = adjusted / 60
            newM = adjusted % 60
        } else {
            newH = newMinTotal / 60
            newM = newMinTotal % 60
        }

        // حفظ الوقت الجديد
        prefs.setCurrentSleepTime(newH, newM)

        // جدولة المنبه القادم
        if (newMinTotal > targetMinTotal) {
            AlarmScheduler.scheduleAlarm(context, newH, newM)
        } else {
            // وصلنا للهدف! نجدول 10:30 مساءً للأبد
            AlarmScheduler.scheduleAlarm(context, 22, 30)
        }
    }

    private fun lockScreen(context: Context) {
        try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val admin = ComponentName(context, AdminReceiver::class.java)
            if (dpm.isAdminActive(admin)) {
                dpm.lockNow()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun normalizeMinutes(h: Int, m: Int): Int {
        return if (h < 6) (h + 24) * 60 + m else h * 60 + m
    }
}
