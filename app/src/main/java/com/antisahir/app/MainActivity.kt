package com.antisahir.app

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: PrefsManager
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PrefsManager(this)
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, AdminReceiver::class.java)

        setupUI()
        requestAdminIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun setupUI() {
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(false)

        btnStart.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            prefs.setStartSleepTime(hour, minute)
            prefs.setCurrentSleepTime(hour, minute)
            prefs.setStartDate(System.currentTimeMillis())
            prefs.setActive(true)
            AlarmScheduler.scheduleAlarm(this, hour, minute)
            updateUI()
            Toast.makeText(this, "✅ تم تفعيل المكافح! سيتقدم وقت نومك 15 دقيقة كل يوم.", Toast.LENGTH_LONG).show()
        }

        btnReset.setOnClickListener {
            prefs.reset()
            AlarmScheduler.cancelAlarm(this)
            updateUI()
            Toast.makeText(this, "🔄 تم إعادة الضبط", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvToday = findViewById<TextView>(R.id.tvToday)
        val tvDaysLeft = findViewById<TextView>(R.id.tvDaysLeft)
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        if (prefs.isActive()) {
            val curH = prefs.getCurrentHour()
            val curM = prefs.getCurrentMinute()

            val targetMinTotal = 22 * 60 + 30
            val curMinTotal = normalizeMinutes(curH, curM)
            val startH = prefs.getStartHour()
            val startM = prefs.getStartMinute()
            val startMinTotal = normalizeMinutes(startH, startM)

            val totalReduction = startMinTotal - targetMinTotal
            val currentReduction = startMinTotal - curMinTotal
            val progressPct = if (totalReduction > 0) (currentReduction * 100 / totalReduction).coerceIn(0, 100) else 100
            val daysLeft = if (curMinTotal > targetMinTotal) (curMinTotal - targetMinTotal) / 15 else 0

            tvStatus.text = if (curMinTotal <= targetMinTotal) "🏆 وصلت للهدف! نوم صحي ✅" else "🌙 رحلة نحو نوم أفضل"
            tvToday.text = formatTime(curH, curM)
            tvDaysLeft.text = if (daysLeft == 0) "وصلت!" else "$daysLeft يوم"
            tvProgress.text = "$progressPct%"
            progressBar.progress = progressPct
        } else {
            tvStatus.text = "⏰ اضبط وقت نومك الحالي للبدء"
            tvToday.text = "--:--"
            tvDaysLeft.text = "-"
            tvProgress.text = "0%"
            progressBar.progress = 0
        }
    }

    private fun normalizeMinutes(h: Int, m: Int): Int {
        return if (h < 6) (h + 24) * 60 + m else h * 60 + m
    }

    private fun formatTime(h: Int, m: Int): String {
        val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m) }
        return SimpleDateFormat("hh:mm a", Locale("ar")).format(cal.time)
    }

    private fun requestAdminIfNeeded() {
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "يحتاج التطبيق لهذا الإذن لإغلاق الشاشة تلقائياً عند حلول وقت النوم")
            }
            startActivity(intent)
        }
    }
}
