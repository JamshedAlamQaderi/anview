package com.jamshedalamqaderi.anview.ext

import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityNodeInfoExt {

    fun AccessibilityNodeInfo.findNodeList(query: String) {

    }

    fun AccessibilityNodeInfo.findNode(query: String) {

    }

    fun AccessibilityNodeInfo?.toAnViewString(): String {
        if (this == null) return ""
        return StringBuilder().apply {
            append("[packageName = ${packageName}; ")
            append("className = $className; ")
            append("text = ${text?.toString()?.replace("\n", "\\n") ?: ""}; ")
            append("error = $error; ")
            append("maxTextLength = $maxTextLength; ")
            append("contentDescription = ${contentDescription?.toString()?.replace("\n", "\\n") ?: ""}; ")
            append("viewIdResName = $viewIdResourceName; ")
            append("checkable = $isCheckable; ")
            append("checked = $isChecked; ")
            append("focusable = $isFocusable; ")
            append("focused = $isFocused; ")
            append("selected = $isSelected;  ")
            append("clickable = $isClickable;  ")
            append("longClickable = $isLongClickable; ")
            append("enabled = $isEnabled; ")
            append("password = $isPassword; ")
            append("scrollable = $isScrollable; ")
            append("visible = $isVisibleToUser; ")
            append("actions = [${this@toAnViewString.actionList?.joinToString(", ") ?: ""}] ")
            append("] ")
        }.toString()
    }

    fun AccessibilityNodeInfo.toTreeString(): String {
        val appender = StringBuilder()
        asTree(appender, this, true, childIndex = null)
        return appender.toString()
    }

    private fun asTree(
        appender: StringBuilder,
        nodeInfo: AccessibilityNodeInfo?,
        isParent: Boolean = false,
        indent: String = "",
        childIndex: Int? = null
    ) {
        if (isParent) {
            appender.append(">>---: ${nodeInfo.toAnViewString()}\n")
        } else {
            appender.append("$indent+---($childIndex): ${nodeInfo?.toAnViewString()}\n")
        }
        for (index in 0 until (nodeInfo?.childCount ?: 0)) {
            asTree(
                appender, nodeInfo?.getChild(index), indent = "$indent\t", childIndex = index
            )
        }
    }
}