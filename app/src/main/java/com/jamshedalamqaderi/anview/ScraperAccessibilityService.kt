package com.jamshedalamqaderi.anview

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import com.jamshedalamqaderi.anview.services.AnViewAccessibilityService

class ScraperAccessibilityService : AnViewAccessibilityService() {
    companion object {
        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val enabled = try {
                val res = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                )
                res > 0
            } catch (e: Exception) {
                false
            }
            if (enabled) {
                val settingValue = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (settingValue != null) {
                    val automationAccessibilityService =
                        context.packageName + "/" + ScraperAccessibilityService::class.java.canonicalName
                    val splitter = TextUtils.SimpleStringSplitter(':')
                    splitter.setString(settingValue)
                    while (splitter.hasNext()) {
                        val accessibilityService = splitter.next()
                        if (accessibilityService.equals(automationAccessibilityService, true)) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }
}