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
import kotlin.time.Duration.Companion.milliseconds

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

    private var timer: Timer? = null

    override fun onServiceConnected() {
        initAnView()
    }

    protected fun initAnView(refreshPeriod: Duration = 100.milliseconds) {
        instance = this
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                setRootWindow(rootInActiveWindow)
                observerList.forEach { observer ->
                    if (observer.isReadyToNotify()) {
                        observer.observer(currentRootWindow)
                    }
                }
            }
        }
        timer?.scheduleAtFixedRate(timerTask, 0, refreshPeriod.inWholeMilliseconds)
    }

    protected fun initAnView(
        timerTask: TimerTask,
        refreshPeriod: Duration = 100.milliseconds
    ) {
        instance = this
        timer = Timer().apply {
            scheduleAtFixedRate(timerTask, 0, refreshPeriod.inWholeMilliseconds)
        }
    }

    protected fun setRootWindow(rootView: AccessibilityNodeInfo?) {
        currentRootWindow = rootView
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            setRootWindow(rootInActiveWindow)
        } else if (event?.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            setRootWindow(rootInActiveWindow)
        }
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        currentRootWindow = null
        instance = null
        timer?.cancel()
    }
}
