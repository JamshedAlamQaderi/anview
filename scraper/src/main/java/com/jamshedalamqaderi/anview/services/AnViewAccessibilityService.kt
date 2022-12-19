package com.jamshedalamqaderi.anview.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.entities.AnViewObserver
import com.jamshedalamqaderi.anview.exceptions.AnViewObserverAlreadyRegisteredException
import java.util.Timer
import java.util.TimerTask
import kotlin.time.Duration

abstract class AnViewAccessibilityService : AccessibilityService() {
    companion object {
        private var instance: AnViewAccessibilityService? = null
        private var currentRootWindow: AccessibilityNodeInfo? = null
        private val observerList = arrayListOf<AnViewObserver>()

        fun registerViewObserver(
            tag: String,
            periodic: Duration,
            observer: (AccessibilityNodeInfo?) -> Unit
        ) {
            if (observerList.find { it.tag == tag } == null) {
                observerList.add(AnViewObserver(tag, periodic, observer))
            } else {
                throw AnViewObserverAlreadyRegisteredException(tag)
            }
        }

        fun removeViewObserver(tag: String) {
            val observer = observerList.find { it.tag == tag }
            if (observer != null) {
                observerList.remove(observer)
            }
        }

        fun currentView(): AccessibilityNodeInfo? {
            return currentRootWindow
        }

        fun getInstance(): AnViewAccessibilityService? {
            return instance
        }
    }

    private var enabledDebugMode = false

    override fun onServiceConnected() {
        instance = this
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                currentRootWindow = rootInActiveWindow
                observerList.forEach { observer ->
                    if (observer.isReadyToNotify()) {
                        observer.observer(currentRootWindow)
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 100)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        currentRootWindow = null
        observerList.clear()
        instance = null
    }
}
