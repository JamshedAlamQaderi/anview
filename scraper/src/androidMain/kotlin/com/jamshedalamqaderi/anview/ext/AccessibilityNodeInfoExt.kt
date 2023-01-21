package com.jamshedalamqaderi.anview.ext

import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.entities.QueryParam
import com.jamshedalamqaderi.anview.enums.ParamType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object AccessibilityNodeInfoExt {
    private val NULL = "NULL"

    fun AccessibilityNodeInfo.toAnViewString(): String {
        return StringBuilder().apply {
            append("[packageName = $packageName; ")
            append("className = $className; ")
            append("text = ${text?.toString()?.replace("\n", "\\n") ?: ""}; ")
            append("error = $error; ")
            append("maxTextLength = $maxTextLength; ")
            append(
                "contentDescription = ${
                    contentDescription?.toString()?.replace("\n", "\\n") ?: ""
                }; "
            )
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
            appender.append(">>---: ${nodeInfo?.toAnViewString()}\n")
        } else {
            appender.append("$indent+---($childIndex): ${nodeInfo?.toAnViewString()}\n")
        }
        for (index in 0 until (nodeInfo?.childCount ?: 0)) {
            asTree(
                appender, nodeInfo?.getChild(index), indent = "$indent\t", childIndex = index
            )
        }
    }

    fun AccessibilityNodeInfo.findNodes(query: QueryNode): List<AccessibilityNodeInfo> {
        val nodeList = arrayListOf<AccessibilityNodeInfo>()
        findNodeList(query, nodeList)
        return nodeList
    }

    private fun AccessibilityNodeInfo.findNodeList(
        query: QueryNode,
        resultNodes: ArrayList<AccessibilityNodeInfo>,
        currentNodeIndex: Int? = null
    ) {
        val node = traverseTopToBottom(query, this, currentNodeIndex)
        if (node != null) {
            resultNodes.add(node)
            return
        }
        forEachChild { childNodeIndex, childNode ->
            childNode?.findNodeList(query, resultNodes, childNodeIndex)
        }
    }

    fun AccessibilityNodeInfo.findNode(query: QueryNode, skip: Int = -1): AccessibilityNodeInfo? {
        val matchedList = findNodes(query)
        if (skip >= 0) {
            return matchedList
                .filterIndexed { index, _ -> index > skip }
                .firstOrNull()
        }
        return matchedList.firstOrNull()
    }

    private fun traverseTopToBottom(
        query: QueryNode,
        nodeInfo: AccessibilityNodeInfo,
        currentNodeIndex: Int? = null
    ): AccessibilityNodeInfo? {
        val isMatched = query.params.map { param ->
            matchWithParamType(param, nodeInfo, currentNodeIndex)
        }.reduceOrNull { acc, b -> acc && b } ?: false
        if (isMatched) {
            if (query.child == null) return nodeInfo
            var resultNode: AccessibilityNodeInfo? = null
            nodeInfo.forEachChild { nodeIndex, childNode ->
                val matched = traverseTopToBottom(query.child, childNode!!, nodeIndex)
                if (matched != null) {
                    resultNode = matched
                    return@forEachChild
                }
            }
            return if (resultNode != null) {
                resultNode
            } else if (query.isOptional == true) {
                nodeInfo
            } else null
        }
        return null
    }

    private fun matchWithParamType(
        param: QueryParam,
        nodeInfo: AccessibilityNodeInfo?,
        nodeInfoIndex: Int? = null
    ): Boolean {
        when (param.paramType) {
            ParamType.actions -> {
                val paramActions: List<AccessibilityNodeInfo.AccessibilityAction> =
                    Json.decodeFromString(param.value ?: "[]")
                return paramActions
                    .map { nodeInfo?.actionList?.contains(it) }
                    .reduceOrNull { acc, b -> acc == true && b == true } ?: false
            }
            else -> {}
        }

        return when (param.paramType) {
            ParamType.packageName -> nodeInfo?.packageName?.toString() ?: NULL
            ParamType.className -> nodeInfo?.className?.toString() ?: NULL
            ParamType.text -> nodeInfo?.text?.toString() ?: NULL
            ParamType.error -> nodeInfo?.error?.toString() ?: NULL
            ParamType.maxTextLength -> nodeInfo?.maxTextLength?.toString() ?: NULL
            ParamType.contentDescription -> nodeInfo?.contentDescription?.toString() ?: NULL
            ParamType.viewIdResName -> nodeInfo?.viewIdResourceName?.toString() ?: NULL
            ParamType.checkable -> nodeInfo?.isCheckable?.toString() ?: NULL
            ParamType.checked -> nodeInfo?.isChecked?.toString() ?: NULL
            ParamType.focusable -> nodeInfo?.isFocusable?.toString() ?: NULL
            ParamType.focused -> nodeInfo?.isFocused?.toString() ?: NULL
            ParamType.selected -> nodeInfo?.isSelected?.toString() ?: NULL
            ParamType.clickable -> nodeInfo?.isClickable?.toString() ?: NULL
            ParamType.longClickable -> nodeInfo?.isLongClickable?.toString() ?: NULL
            ParamType.enabled -> nodeInfo?.isEnabled?.toString() ?: NULL
            ParamType.password -> nodeInfo?.isPassword?.toString() ?: NULL
            ParamType.scrollable -> nodeInfo?.isScrollable?.toString() ?: NULL
            ParamType.visible -> nodeInfo?.isVisibleToUser?.toString() ?: NULL
            ParamType.nodeIndex -> nodeInfoIndex?.toString() ?: NULL
            else -> NULL
        }.isMatched(param.value)
    }

    fun AccessibilityNodeInfo.forEachChild(block: (Int, AccessibilityNodeInfo?) -> Unit) {
        for (index in 0 until childCount) {
            block(index, getChild(index))
        }
    }
}
