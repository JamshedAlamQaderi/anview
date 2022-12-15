package com.jamshedalamqaderi.anview.ext

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.jamshedalamqaderi.anview.services.AnViewAccessibilityService

object AnViewActions {
    fun AccessibilityNodeInfo.click() = performAction(AccessibilityNodeInfo.ACTION_CLICK)

    @RequiresApi(Build.VERSION_CODES.N)
    fun AccessibilityNodeInfo.tap(): Boolean {
        val rect = Rect()
        getBoundsInScreen(rect)
        return dispatchGesture(Path().apply {
            moveTo(rect.exactCenterX(), rect.exactCenterY())
        }, ViewConfiguration.getTapTimeout().toLong())
    }

    fun AccessibilityNodeInfo.longClick() = performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)

    @RequiresApi(Build.VERSION_CODES.N)
    fun AccessibilityNodeInfo.longTap(): Boolean {
        val rect = Rect()
        getBoundsInScreen(rect)
        return dispatchGesture(Path().apply {
            moveTo(rect.exactCenterX(), rect.exactCenterY())
        }, ViewConfiguration.getLongPressTimeout().toLong())
    }

    fun AccessibilityNodeInfo.swipeForward() =
        performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)

    fun AccessibilityNodeInfo.swipeBackward() =
        performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)

    @RequiresApi(Build.VERSION_CODES.N)
    fun AccessibilityNodeInfo.swipeUp(): Boolean {
        val rect = Rect()
        getBoundsInScreen(rect)
        return swipe(
            rect.exactCenterX(),
            rect.bottom.toFloat(),
            rect.exactCenterX(),
            rect.top.toFloat()
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun AccessibilityNodeInfo.swipeDown(): Boolean {
        val rect = Rect()
        getBoundsInScreen(rect)
        return swipe(
            rect.exactCenterX(),
            rect.top.toFloat(),
            rect.exactCenterX(),
            rect.bottom.toFloat()
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun AccessibilityNodeInfo.swipeRight(): Boolean {
        val rect = Rect()
        getBoundsInScreen(rect)
        return swipe(
            rect.left.toFloat(),
            rect.exactCenterY(),
            rect.right.toFloat(),
            rect.exactCenterY()
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun AccessibilityNodeInfo.swipeLeft(): Boolean {
        val rect = Rect()
        getBoundsInScreen(rect)
        return swipe(
            rect.right.toFloat(),
            rect.exactCenterY(),
            rect.left.toFloat(),
            rect.exactCenterY()
        )
    }

    fun AccessibilityNodeInfo.inputText(text: String) =
        performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
            putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
        })

    fun pressBack(): Boolean {
        return AnViewAccessibilityService
            .getInstance()
            ?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK) ?: false
    }

    fun pressHome(): Boolean {
        return AnViewAccessibilityService
            .getInstance()
            ?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME) ?: false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun swipe(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
        if (startX < 0 || startY < 0 || endX < 0 || endY < 0) return false
        return dispatchGesture(Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }, ViewConfiguration.getScrollDefaultDelay().toLong())
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchGesture(path: Path, duration: Long): Boolean {
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0L, duration))
        return AnViewAccessibilityService.getInstance()
            ?.dispatchGesture(gestureBuilder.build(), null, null) ?: false
    }
}