package com.jamshedalamqaderi.anview

import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import com.jamshedalamqaderi.anview.bot.SendMessageBot
import com.jamshedalamqaderi.anview.ext.AccessibilityNodeInfoExt.toTreeString
import com.jamshedalamqaderi.anview.services.AnViewAccessibilityService
import kotlin.time.Duration.Companion.milliseconds

class ScraperAccessibilityService : AnViewAccessibilityService() {
    companion object {
        fun isEnabled(context: Context): Boolean {
            if (Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                ) > 0
            ) {
                val settingValue = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                return settingValue?.split(":")
                    ?.contains("${context.packageName}/${this::class.qualifiedName}")
                    ?: false
            }
            return false
        }
    }

    private val viewDebugMode = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        // AnViewAccessibilityService.registerViewObserver() can be called from anywhere
        // On every 500 millisecond will be called with AccessibilityNodeInfo argument
        if (!viewDebugMode) {
            val bot = SendMessageBot(this, "2222", "Hello, AnView")
            registerViewObserver("MESSAGE_BOT", 500L.milliseconds) { accessibilityNodeInfo ->
                bot.onUpdate(accessibilityNodeInfo)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        super.onAccessibilityEvent(event)
        if (viewDebugMode) {
            println(currentView()?.toTreeString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeViewObserver("MESSAGE_BOT")
    }
}
