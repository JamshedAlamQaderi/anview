package com.jamshedalamqaderi.anview.entities

import android.view.accessibility.AccessibilityNodeInfo
import kotlin.time.Duration

data class AnViewObserver(
    val tag: String,
    val period: Duration,
    val observer: (AccessibilityNodeInfo?) -> Unit,
) {
    private var lastNotificationTime: Long? = null

    fun isReadyToNotify(currentTime: Long = System.currentTimeMillis()): Boolean {
        if (lastNotificationTime == null || (
            (
                (
                    lastNotificationTime
                        ?: 0
                    ) + period.inWholeMilliseconds
                ) <= currentTime
            )
        ) {
            lastNotificationTime = currentTime
            return true
        }
        return false
    }
}
