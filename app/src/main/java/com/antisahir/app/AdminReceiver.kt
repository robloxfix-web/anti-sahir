package com.antisahir.app

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(context, "✅ صلاحية الإدارة مُفعَّلة - مكافح السهر جاهز!", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "⚠️ تم إلغاء صلاحية الإدارة - لن يعمل الإغلاق التلقائي", Toast.LENGTH_SHORT).show()
    }
}
