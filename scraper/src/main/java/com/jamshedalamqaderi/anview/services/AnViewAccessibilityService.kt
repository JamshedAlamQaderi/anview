package com.jamshedalamqaderi.anview.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

abstract class AnViewAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }
}